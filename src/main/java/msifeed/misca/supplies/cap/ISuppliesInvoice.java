package msifeed.misca.supplies.cap;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface ISuppliesInvoice {
    long getLastDeliveryTime();
    void setLastDeliveryTime(long value);

    long getDeliveryInterval();
    void setDeliveryInterval(long value);

    int getMaxDeliverySequence();
    void setMaxDeliverySequence(int value);

    NonNullList<ItemStack> getProducts();
    void setProducts(NonNullList<ItemStack> list);

    default boolean isEmpty() {
        return getProducts().isEmpty();
    }

    default void replaceWith(ISuppliesInvoice invoice) {
        this.setLastDeliveryTime(System.currentTimeMillis());
        this.setDeliveryInterval(invoice.getDeliveryInterval());
        this.setMaxDeliverySequence(invoice.getMaxDeliverySequence());
        this.setProducts(invoice.getProducts());
    }
}
