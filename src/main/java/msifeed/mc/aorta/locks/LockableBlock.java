package msifeed.mc.aorta.locks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface LockableBlock {
    default TileEntity getLockTileEntity(World world, int x, int y, int z) {
        final TileEntity targetTile = world.getTileEntity(x, y, z);
        return targetTile instanceof Lockable
                ? targetTile
                : null;
    }

    default LockObject getLock(World world, int x, int y, int z) {
        final TileEntity te = getLockTileEntity(world, x, y, z);
        return te instanceof Lockable
                ?((Lockable) te).getLock()
                : null;
    }

    default boolean dropLockOnBreak(World world, int x, int y, int z) {
        return true;
    }
}
