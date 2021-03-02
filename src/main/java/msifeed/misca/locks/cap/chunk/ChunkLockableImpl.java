package msifeed.misca.locks.cap.chunk;

import msifeed.misca.locks.cap.lock.ILockable;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class ChunkLockableImpl implements IChunkLockable {
    private final Map<BlockPos, ILockable> locks = new HashMap<>();

    @Override
    public Map<BlockPos, ILockable> getLocks() {
        return locks;
    }
}
