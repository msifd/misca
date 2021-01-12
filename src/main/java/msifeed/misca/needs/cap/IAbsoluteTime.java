package msifeed.misca.needs.cap;

import msifeed.misca.Misca;
import net.minecraft.util.ResourceLocation;

public interface IAbsoluteTime {
    ResourceLocation KEY = new ResourceLocation(Misca.MODID, "time");

    long getTime();
    void setTime(long value);

    /**
     * @return seconds passed from stored time
     */
    default long consumeTime() {
        final long now = System.currentTimeMillis() / 1000;
        final long diff = now - getTime();
        setTime(now);
        return diff;
    }

    default void replaceWith(IAbsoluteTime other) {
        this.setTime(other.getTime());
    }
}