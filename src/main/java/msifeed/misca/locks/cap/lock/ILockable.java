package msifeed.misca.locks.cap.lock;

import msifeed.misca.Misca;
import msifeed.misca.locks.LockType;
import net.minecraft.util.ResourceLocation;

public interface ILockable {
    ResourceLocation KEY = new ResourceLocation(Misca.MODID, "lock");

    LockType getType();

    void setType(LockType type);

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
}
