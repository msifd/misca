package msifeed.misca.supplies;

import msifeed.misca.Misca;
import msifeed.misca.supplies.cap.ISuppliesInvoice;
import msifeed.misca.supplies.cap.SuppliesInvoice;
import msifeed.misca.supplies.cap.SuppliesInvoiceProvider;
import msifeed.misca.supplies.cap.SuppliesInvoiceStorage;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BackgroundSupplies {
    public static final ResourceLocation CAP = new ResourceLocation(Misca.MODID, "supplies");

    public void preInit() {
        CapabilityManager.INSTANCE.register(ISuppliesInvoice.class, new SuppliesInvoiceStorage(), SuppliesInvoice::new);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onAttachTileCapability(AttachCapabilitiesEvent<TileEntity> event) {
        if (event.getObject() instanceof IInventory)
            event.addCapability(CAP, new SuppliesInvoiceProvider());
    }

    @SubscribeEvent
    public void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getWorld().isRemote) return;

        final TileEntity tile = event.getWorld().getTileEntity(event.getPos());
        if (!(tile instanceof IInventory)) return;
        final ISuppliesInvoice supplies = SuppliesInvoiceProvider.get(tile);
        if (supplies == null) return;

        for (ItemStack s : BackgroundSupplies.gatherDelivery(supplies)) {
            storeProduct((IInventory) tile, s);
        }
    }

    public static List<ItemStack> gatherDelivery(ISuppliesInvoice supplies) {
        final long interval = supplies.getDeliveryInterval();
        if (interval < 1)
            return Collections.emptyList();

        final long lastDelivery = supplies.getLastDeliveryIndex();
        final long currentDelivery = supplies.currentDeliveryIndex();
        final int maxSequence = supplies.getMaxDeliverySequence();
        final int deliveries = Math.min((int) (currentDelivery - lastDelivery), maxSequence);
        if (deliveries < 1)
            return Collections.emptyList();

        final List<ItemStack> items = new ArrayList<>();

        for (ISuppliesInvoice.Batch batch : supplies.getBatches()) {
            for (ItemStack stack : batch.getProductsWithChance()) {
                final ItemStack joinedStack = stack.copy();
                joinedStack.setCount(stack.getCount() * deliveries);
                mergeStack(items, joinedStack);

                if (joinedStack.getCount() > 0) // inventory is full
                    break;
            }
        }

        supplies.setLastDeliveryIndex(currentDelivery);

        return items;
    }

    private static void mergeStack(List<ItemStack> items, ItemStack stack) {
        for (ItemStack slot : items) {
            if (!canMergeStacks(slot, stack)) continue;

            final int share = Math.min(slot.getMaxStackSize() - slot.getCount(), stack.getCount());
            stack.shrink(share);
            slot.grow(share);

            if (stack.isEmpty()) return;
        }

        if (!stack.isEmpty())
            items.add(stack);
    }

    private static boolean canMergeStacks(ItemStack base, ItemStack stack2) {
        return base.isStackable() && base.getCount() < base.getMaxStackSize() && stackEqualExact(base, stack2);
    }

    private static boolean stackEqualExact(ItemStack left, ItemStack right) {
        return left.getItem() == right.getItem() && (!left.getHasSubtypes() || left.getMetadata() == right.getMetadata()) && ItemStack.areItemStackTagsEqual(left, right);
    }

    private static void storeProduct(IInventory inv, ItemStack joinedStack) {
        final int invSize = inv.getSizeInventory();
        final int stackLimit = inv.getInventoryStackLimit();

        for (int i = 0; i < invSize; i++) {
            final ItemStack slot = inv.getStackInSlot(i);

            if (slot.isEmpty()) {
                final int share = Math.min(stackLimit, joinedStack.getCount());
                final ItemStack part = joinedStack.copy();
                joinedStack.shrink(share);
                part.setCount(share);
                inv.setInventorySlotContents(i, part);
            } else {
                if (slot.getCount() >= stackLimit || !slot.isItemEqual(joinedStack) || !ItemStack.areItemStackTagsEqual(slot, joinedStack))
                    continue;

                final int share = Math.min(stackLimit - slot.getCount(), joinedStack.getCount());
                joinedStack.shrink(share);
                slot.grow(share);
                inv.setInventorySlotContents(i, slot);
            }

            if (joinedStack.isEmpty())
                return;
        }
    }

    public static List<String> getAbsoluteInfoLines(ISuppliesInvoice invoice) {
        final List<String> lines = new ArrayList<>();

        lines.add(String.format("Interval: %d min", invoice.getDeliveryInterval() / 60000));
        lines.add("Max Sequence: " + invoice.getMaxDeliverySequence());

        for (ISuppliesInvoice.Batch batch : invoice.getBatches()) {
            final String prods = batch.products.stream().limit(5)
                    .map(is -> String.format("%d*%s", is.getCount(), is.getDisplayName()))
                    .collect(Collectors.joining(", "));
            lines.add(String.format("* %.0f%% [%s]", batch.chance * 100, prods));
        }

        return lines;
    }

    public static List<String> getRelativeInfoLines(ISuppliesInvoice invoice) {
        final List<String> lines = new ArrayList<>();

        final long interval = Math.max(invoice.getDeliveryInterval(), 1);
        final long lastDelivery = invoice.getLastDeliveryIndex();
        final long currentDelivery = invoice.currentDeliveryIndex();
        final int maxSequence = invoice.getMaxDeliverySequence();
        final int deliveries = Math.min((int) (currentDelivery - lastDelivery), maxSequence);
        final double nextSupply = (interval - System.currentTimeMillis() % interval) / 60000d;
        final int stacks = invoice.getBatches().stream()
                .mapToInt(batch -> batch.products.stream()
                        .mapToInt(s -> (int) Math.ceil(s.getCount() * deliveries / (double) s.getMaxStackSize()))
                        .sum()
                ).sum();

        if (deliveries < maxSequence)
            lines.add(String.format("Supplies %d/%d. Next in %.1f min", deliveries, maxSequence, nextSupply));
        else
            lines.add(String.format("Supplies %d/%d", deliveries, maxSequence));

        lines.add(String.format("Stacks of supplies: %d", stacks));

        for (ISuppliesInvoice.Batch batch : invoice.getBatches()) {
            final String prods = batch.products.stream().limit(5)
                    .map(is -> String.format("%d*%s", is.getCount(), is.getDisplayName()))
                    .collect(Collectors.joining(", "));
            lines.add(String.format("* %.0f%% [%s]", batch.chance * 100, prods));
        }

        return lines;
    }
}
