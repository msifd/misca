package msifeed.misca.supplies.cap;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class SuppliesInvoice implements ISuppliesInvoice {
    private long lastDelivery = 0;
    private long interval = 0;
    private int maxSequence = 0;
    private NonNullList<ItemStack> products = NonNullList.create();

    @Override
    public long getLastDeliveryTime() {
        return lastDelivery;
    }

    @Override
    public void setLastDeliveryTime(long value) {
        this.lastDelivery = value;
    }

    @Override
    public long getDeliveryInterval() {
        return interval;
    }

    @Override
    public void setDeliveryInterval(long value) {
        this.interval = value;
    }

    @Override
    public int getMaxDeliverySequence() {
        return maxSequence;
    }

    @Override
    public void setMaxDeliverySequence(int value) {
        this.maxSequence = value;
    }

    @Override
    public NonNullList<ItemStack> getProducts() {
        return products;
    }

    @Override
    public void setProducts(NonNullList<ItemStack> list) {
        this.products = list;
    }
}
