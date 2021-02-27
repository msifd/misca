package msifeed.misca.locks.cap.pick;

import net.minecraft.util.math.BlockPos;

public interface ILockPick {
    BlockPos getPos();

    int getSecret();

    void setLock(BlockPos pos, int secret);

    void setPin(int pos);

    void reset();

    default boolean isLocked() {
        return getSecret() != 0;
    }
}
