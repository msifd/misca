package msifeed.misca.locks.cap.chunk;

import msifeed.misca.Misca;
import msifeed.misca.locks.cap.tile.ILockable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.util.Map;

public interface IChunkLockable {
    ResourceLocation KEY = new ResourceLocation(Misca.MODID, "locky");

    Map<BlockPos, ILockable> getLocks();

    default ILockable getLock(BlockPos pos) {
        return getLocks().get(pos);
    }

    default void addLock(BlockPos pos, ILockable lock) {
        getLocks().put(pos, lock);
    }

    default boolean removeLock(BlockPos pos) {
        return getLocks().remove(pos) != null;
    }

    default void replaceWith(IChunkLockable locks) {
        getLocks().clear();
        getLocks().putAll(locks.getLocks());
    }
}
