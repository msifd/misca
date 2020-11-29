package msifeed.misca.locks.chunk;

import msifeed.misca.locks.tile.ILockable;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class ChunkLockable implements IChunkLockable {
    private final Map<BlockPos, ILockable> locks = new HashMap<>();

    @Override
    public Map<BlockPos, ILockable> getLocks() {
        return locks;
    }
}
