package msifeed.misca.supplies.cap;

import java.util.ArrayList;
import java.util.List;

public class SuppliesInvoice implements ISuppliesInvoice {
    private long lastDelivery = 0;
    private long interval = 0;
    private int maxSequence = 0;
    private final List<Batch> batches = new ArrayList<>();

    @Override
    public long getLastDeliveryIndex() {
        return lastDelivery;
    }

    @Override
    public void setLastDeliveryIndex(long value) {
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
    public List<Batch> getBatches() {
        return batches;
    }

    @Override
    public void addBatch(Batch batch) {
        batches.add(batch);
    }
}
