package msifeed.misca.supplies;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

public class SuppliesInvoice {
    public long lastDelivery = 0;
    public long interval = 0;
    public int maxSequence = 0;

    public NonNullList<ItemStack> products = NonNullList.create();

    public SuppliesInvoice() {
    }

    public void writeToNBT(NBTTagCompound compound) {
        compound.setLong("Last", lastDelivery);
        compound.setLong("Int", interval);
        compound.setInteger("MaxSeq", maxSequence);
        ItemStackHelper.saveAllItems(compound, products);
    }

    public void readFromNBT(NBTTagCompound compound) {
        lastDelivery = compound.getLong("Last");
        interval = compound.getInteger("Int");
        maxSequence = compound.getInteger("MaxSeq");

        products = NonNullList.withSize(compound.getTagList("Items", 10).tagCount(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, products);
    }

    public void deliver(IInventory inv) {
        if (interval < 1)
            return;

        final long now = System.currentTimeMillis();
        final int deliveries = (int) Math.min((now - lastDelivery) / interval, maxSequence);
        if (deliveries < 1)
            return;

        for (ItemStack stack : products) {
            final ItemStack joinedStack = stack.copy();
            joinedStack.setCount(stack.getCount() * deliveries);
            storeProduct(inv, joinedStack);
        }

        lastDelivery = now;
    }

    private void storeProduct(IInventory inv, ItemStack joinedStack) {
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
}
