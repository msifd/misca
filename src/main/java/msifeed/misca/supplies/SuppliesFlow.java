package msifeed.misca.supplies;

import msifeed.misca.charstate.handler.StaminaHandler;
import msifeed.misca.supplies.cap.ISuppliesInvoice;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SuppliesFlow {
    public static void makeDelivery(EntityPlayer player, ISuppliesInvoice invoice, IItemHandler inv, int maxOverride) {
        final int times = getDeliveryTimes(player, invoice, maxOverride);
        if (times <= 0) return;

        for (ItemStack s : gatherDeliveries(invoice, times)) {
            storeProduct(inv, s);
        }

        updateDeliveryIndex(invoice, times);
        StaminaHandler.consumeDelivery(player, invoice.getDeliveryPrice() * times);
    }

    public static int getDeliveryTimes(EntityPlayer player, ISuppliesInvoice invoice, int maxOverride) {
        final double stamina = StaminaHandler.getStaminaForDelivery(player);
        final int max = Math.min(maxDeliveries(invoice, stamina), maxOverride);
        final int scheduled = scheduledDeliveries(invoice);
        return Math.min(scheduled, max);
    }

    public static void updateDeliveryIndex(ISuppliesInvoice invoice, int times) {
        final int potentialDeliveries = Math.min((int) (invoice.currentDeliveryIndex() - invoice.getLastDeliveryIndex()), invoice.getMaxDeliverySequence());
        final long indexOffset = potentialDeliveries - times;
        invoice.setLastDeliveryIndex(invoice.currentDeliveryIndex() - indexOffset);
    }

    private static int maxDeliveries(ISuppliesInvoice supplies, double stamina) {
        final int maxSequence = supplies.getMaxDeliverySequence();
        final int maxStamina = (int) Math.floor(stamina / supplies.getDeliveryPrice());
        return Math.min(maxSequence, maxStamina);
    }

    private static int scheduledDeliveries(ISuppliesInvoice supplies) {
        final long lastDelivery = supplies.getLastDeliveryIndex();
        final long currentDelivery = supplies.currentDeliveryIndex();
        return (int) (currentDelivery - lastDelivery);
    }

    public static List<ItemStack> gatherDeliveries(ISuppliesInvoice supplies, int times) {
        if (times <= 0)
            return Collections.emptyList();

        final List<ItemStack> items = new ArrayList<>();
        for (ISuppliesInvoice.Batch batch : supplies.getBatches()) {
            items.addAll(batch.getProductsWithChance(times));
        }

        return items;
    }

    private static void storeProduct(IItemHandler inv, ItemStack stack) {
        if (stack.isEmpty())
            return;

        for (int i = 0; i < inv.getSlots(); i++) {
            final ItemStack remainder = inv.insertItem(i, stack, false);
            if (remainder.isEmpty()) {
                return;
            } else {
                stack = remainder;
            }
        }
    }

    public static List<String> getAbsoluteInfoLines(ISuppliesInvoice invoice) {
        final List<String> lines = new ArrayList<>();

        lines.add(String.format("Interval: %d min", invoice.getDeliveryInterval() / 60000));
        lines.add("Max Sequence: " + invoice.getMaxDeliverySequence());
        lines.add("Stamina per delivery: " + invoice.getDeliveryPrice());

        for (ISuppliesInvoice.Batch batch : invoice.getBatches()) {
            final String prods = batch.products.stream().limit(5)
                    .map(is -> String.format("%d*%s", is.getCount(), is.getDisplayName()))
                    .collect(Collectors.joining(", "));
            lines.add(String.format("* %.0f%% [%s]", batch.chance * 100, prods));
        }

        return lines;
    }

    public static List<String> getRelativeInfoLines(ISuppliesInvoice invoice, double stamina) {
        final List<String> lines = new ArrayList<>();

        final long interval = Math.max(invoice.getDeliveryInterval(), 1);
        final long lastDelivery = invoice.getLastDeliveryIndex();
        final long currentDelivery = invoice.currentDeliveryIndex();
        final int maxSequence = invoice.getMaxDeliverySequence();
        final int maxAvailable = maxDeliveries(invoice, stamina);
        final int deliveries = Math.min((int) (currentDelivery - lastDelivery), maxSequence);
        final double nextSupply = (interval - System.currentTimeMillis() % interval) / 60000d;
        final int stacks = invoice.getBatches().stream()
                .mapToInt(batch -> batch.products.stream()
                        .mapToInt(s -> (int) Math.ceil(s.getCount() * deliveries / (double) s.getMaxStackSize()))
                        .sum()
                ).sum();

        if (deliveries < maxSequence)
            lines.add(String.format("Supplies %d/%d/%d. Next in %.1f min", deliveries, maxAvailable, maxSequence, nextSupply));
        else
            lines.add(String.format("Supplies %d/%d/%d", deliveries, maxAvailable, maxSequence));

        lines.add("Stamina per delivery: " + invoice.getDeliveryPrice());
        lines.add("Stacks of supplies: <= " + stacks);

        for (ISuppliesInvoice.Batch batch : invoice.getBatches()) {
            final String prods = batch.products.stream().limit(5)
                    .map(is -> String.format("%d*%s", is.getCount(), is.getDisplayName()))
                    .collect(Collectors.joining(", "));
            lines.add(String.format("* %.0f%% [%s]", batch.chance * 100, prods));
        }

        return lines;
    }

}
