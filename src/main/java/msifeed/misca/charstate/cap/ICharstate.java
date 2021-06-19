package msifeed.misca.charstate.cap;

import msifeed.misca.Misca;
import msifeed.misca.charsheet.CharEffort;
import msifeed.sys.cap.FloatContainer;
import net.minecraft.util.ResourceLocation;

public interface ICharstate {
    ResourceLocation KEY = new ResourceLocation(Misca.MODID, "state");

    long getUpdateTime();
    void setUpdateTime(long value);
    default long passedFromUpdate() {
        final long now = System.currentTimeMillis() / 1000;
        return now - getUpdateTime();
    }
    default void resetUpdateTime() {
        setUpdateTime(System.currentTimeMillis() / 1000);
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

    FloatContainer<CharEffort> efforts();

    int nonce();
    void incNonce();
    void setNonce(int value);

    void replaceWith(ICharstate other);
}