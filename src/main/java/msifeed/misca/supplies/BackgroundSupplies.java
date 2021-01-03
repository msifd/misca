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
        if (supplies != null)
            deliver(supplies, (IInventory) tile);
    }

    public static void deliver(ISuppliesInvoice supplies, IInventory inv) {
        final long interval = supplies.getDeliveryInterval();
        if (interval < 1)
            return;

        final long lastDelivery = supplies.getLastDeliveryTime();
        final long maxSequence = supplies.getMaxDeliverySequence();
        final long now = System.currentTimeMillis();
        final int deliveries = (int) Math.min((now - lastDelivery) / interval, maxSequence);
        if (deliveries < 1)
            return;

        for (ItemStack stack : supplies.getProducts()) {
            final ItemStack joinedStack = stack.copy();
            joinedStack.setCount(stack.getCount() * deliveries);
            storeProduct(inv, joinedStack);

            if (joinedStack.getCount() > 0) // inventory is full
                break;
        }

        supplies.setLastDeliveryTime(now);
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

            if (joinedStack.getCount() == 0)
                return;
        }
    }
}
