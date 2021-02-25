package msifeed.misca.locks.cap.tile;

import msifeed.misca.Misca;
import msifeed.misca.locks.LockUtils;
import net.minecraft.util.ResourceLocation;

public interface ILockable {
    ResourceLocation KEY = new ResourceLocation(Misca.MODID, "lock");

    boolean isLocked();

    void setLocked(boolean value);

    default boolean hasSecret() {
        return getSecret() != 0;
    }

    int getSecret();

    void setSecret(int value);

    default boolean canOpenWith(int key) {
        return getSecret() == key;
    }

    default int getNumberOfPins() {
        return LockUtils.getNumberOfPins(getSecret());
    }
}
