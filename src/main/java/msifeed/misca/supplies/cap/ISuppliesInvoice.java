package msifeed.misca.supplies.cap;

import msifeed.misca.rolls.Dices;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.ArrayList;
import java.util.List;

public interface ISuppliesInvoice {
    long getLastDeliveryIndex();

    void setLastDeliveryIndex(long value);

    long getDeliveryInterval();

    void setDeliveryInterval(long value);

    int getMaxDeliverySequence();

    void setMaxDeliverySequence(int value);

    List<Batch> getBatches();

    void addBatch(Batch batch);

    default boolean isEmpty() {
        return getBatches().isEmpty();
    }

    default long currentDeliveryIndex() {
        return System.currentTimeMillis() / getDeliveryInterval();
    }

    default void replaceWith(ISuppliesInvoice invoice) {
        this.setLastDeliveryIndex(System.currentTimeMillis());
        this.setDeliveryInterval(invoice.getDeliveryInterval());
        this.setMaxDeliverySequence(invoice.getMaxDeliverySequence());
        this.getBatches().clear();
        for (Batch b : invoice.getBatches())
            this.addBatch(b);
    }

    class Batch {
        public NonNullList<ItemStack> products = NonNullList.create();
        public double chance = 1;

        public List<ItemStack> getProductsWithChance(int times) {
            final List<ItemStack> result = new ArrayList<>();
            for (int i = 0; i < times; i++) {
                if (Dices.check(chance))
                    result.addAll(products);
            }
            return result;
        }
    }
}
