package msifeed.misca.locks.cap.pick;

import msifeed.misca.locks.LockUtils;
import net.minecraft.util.math.BlockPos;

public class LockPickImpl implements ILockPick {
    private BlockPos pos = BlockPos.ORIGIN;
    private int secret = 0;

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public int getSecret() {
        return secret;
    }

    @Override
    public void setLock(BlockPos pos, int secret) {
        this.pos = pos;
        this.secret = secret;
    }

    @Override
    public void setPin(int pos) {
        this.secret = LockUtils.zeroPinPos(secret, pos);
    }

    @Override
    public void reset() {
        this.pos = BlockPos.ORIGIN;
        this.secret = 0;
    }
}
