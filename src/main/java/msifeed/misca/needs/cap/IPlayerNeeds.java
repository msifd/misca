package msifeed.misca.needs.cap;

import msifeed.misca.Misca;
import net.minecraft.util.ResourceLocation;

public interface IPlayerNeeds {
    ResourceLocation KEY = new ResourceLocation(Misca.MODID, "needs");

    long getUpdateTime();
    void setUpdateTime(long value);
    /**
     * @return seconds passed from stored time
     */
    default long consumeTime() {
        final long now = System.currentTimeMillis() / 1000;
        final long diff = now - getUpdateTime();
        setUpdateTime(now);
        return diff;
    }

    long getMiningTime();
    void setMiningTime(long value);
    default long passedFromMining() {
        final long now = System.currentTimeMillis() / 1000;
        return now - getMiningTime();
    }
    default void resetMiningTime() {
        setMiningTime(System.currentTimeMillis() / 1000);
    }

    default void replaceWith(IPlayerNeeds other) {
        this.setUpdateTime(other.getUpdateTime());
        this.setMiningTime(other.getMiningTime());
    }
}