package msifeed.misca.needs.cap;

import msifeed.misca.Misca;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public interface IPlayerNeeds {
    ResourceLocation KEY = new ResourceLocation(Misca.MODID, "needs");

    double get(NeedType needType);
    void set(NeedType needType, double value);

    default void add(NeedType nt, double value) {
        set(nt, nt.clamp(get(nt) + value));
    }

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

    void replaceWith(IPlayerNeeds other);

    enum NeedType {
        integrity(100, 100), sanity(100, 150), stamina(1, 1), corruption(0, 100);

        public final double def, max;

        NeedType(double def, double max) {
            this.def = def;
            this.max = max;
        }

        public double clamp(double value) {
            return MathHelper.clamp(value, 0, max);
        }
    }
}