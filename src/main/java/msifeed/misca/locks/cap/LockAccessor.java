package msifeed.misca.locks.cap;

import msifeed.misca.Misca;
import msifeed.misca.locks.LocksConfig;
import msifeed.misca.locks.LocksRpc;
import msifeed.misca.locks.cap.chunk.ChunkLockableProvider;
import msifeed.misca.locks.cap.chunk.IChunkLockable;
import msifeed.misca.locks.cap.tile.ILockable;
import msifeed.misca.locks.cap.tile.LockableImpl;
import msifeed.misca.locks.cap.tile.LockableProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LockAccessor {
    public static boolean isLocked(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null) {
            if (getBlockLookup(tile.getBlockType()) == LocksConfig.Lookup.adjacent) {
                final BlockPos adjPos = findBestAdjacentBlock(world, pos, tile.getBlockType());
                final TileEntity adjTile = world.getTileEntity(adjPos);
                if (adjTile != null)
                    tile = adjTile;
            }

            final ILockable lock = LockableProvider.get(tile);
            return lock != null && lock.isLocked();
        } else {
            final BlockPos chunkPos = getChunkLockPos(world, pos);
            if (chunkPos == null) return false;

            final Chunk chunk = world.getChunk(chunkPos);
            final ILockable lock = ChunkLockableProvider.get(chunk).getLock(chunkPos);
            return lock != null && lock.isLocked();
        }
    }

    /**
     * @return null if lock can't be here, wrap otherwise
     */
    @Nullable
    public static ILockHolder createWrap(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null) {
            if (getBlockLookup(tile.getBlockType()) == LocksConfig.Lookup.adjacent) {
                final BlockPos adjPos = findBestAdjacentBlock(world, pos, tile.getBlockType());
                final TileEntity adjTile = world.getTileEntity(adjPos);
                if (adjTile != null)
                    tile = adjTile;
            }

            final ILockable lock = LockableProvider.get(tile);
            if (lock == null) return null;

            return new TileLockWrap(tile, lock);
        } else {
            final BlockPos chunkPos = getChunkLockPos(world, pos);
            if (chunkPos == null) return null;
            return new ChunkLockWrap(world.getChunk(chunkPos), chunkPos);
        }
    }

    public static class TileLockWrap implements ILockHolder {
        private final TileEntity tile;
        private final ILockable lock;

        private TileLockWrap(TileEntity tile, ILockable lock) {
            this.tile = tile;
            this.lock = lock;
        }

        @Override
        public boolean addLock(int secret) {
            if (lock.hasSecret()) return false;

            lock.setLocked(false);
            lock.setSecret(secret);
            tile.markDirty();

            return true;
        }

        @Override
        public boolean removeLock() {
            if (!lock.hasSecret()) return false;

            lock.setLocked(false);
            lock.setSecret(0);
            tile.markDirty();

            return true;
        }

        @Override
        public boolean isLocked() {
            return lock.isLocked();
        }

        @Override
        public void setLocked(boolean value) {
            lock.setLocked(value);
            tile.markDirty();
        }

        @Override
        public int getSecret() {
            return lock.getSecret();
        }

        @Override
        public void setSecret(int value) {
            lock.setSecret(value);
            tile.markDirty();
        }
    }

    public static class ChunkLockWrap implements ILockHolder {
        private final Chunk chunk;
        private final BlockPos pos;
        private final @Nullable ILockable lock;

        private ChunkLockWrap(Chunk chunk, BlockPos pos) {
            this.chunk = chunk;
            this.pos = pos;
            this.lock = ChunkLockableProvider.get(chunk).getLock(pos);
        }

        @Override
        public boolean addLock(int secret) {
            if (lock != null) return false;

            final IChunkLockable chunkLocks = ChunkLockableProvider.get(chunk);
            chunkLocks.addLock(pos, new LockableImpl(false, secret));
            chunk.markDirty();

            if (!chunk.getWorld().isRemote)
                LocksRpc.syncChunkLocks(chunk, ChunkLockableProvider.get(chunk));

            return true;
        }

        @Override
        public boolean removeLock() {
            if (lock == null) return false;

            final IChunkLockable chunkLocks = ChunkLockableProvider.get(chunk);
            if (chunkLocks.removeLock(pos)) {
                chunk.markDirty();

                if (!chunk.getWorld().isRemote)
                    LocksRpc.syncChunkLocks(chunk, ChunkLockableProvider.get(chunk));
            }

            return true;
        }

        @Override
        public boolean isLocked() {
            return lock != null && lock.isLocked();
        }

        @Override
        public void setLocked(boolean value) {
            if (lock == null) return;

            lock.setLocked(value);
            chunk.markDirty();

            if (!chunk.getWorld().isRemote)
                LocksRpc.syncChunkLocks(chunk, ChunkLockableProvider.get(chunk));
        }

        @Override
        public int getSecret() {
            return lock == null ? 0 : lock.getSecret();
        }

        @Override
        public void setSecret(int value) {
            if (lock == null) return;

            lock.setSecret(value);
            chunk.markDirty();

            if (!chunk.getWorld().isRemote)
                LocksRpc.syncChunkLocks(chunk, ChunkLockableProvider.get(chunk));
        }
    }

    @Nullable
    private static BlockPos getChunkLockPos(World world, BlockPos pos) {
        final IBlockState state = world.getBlockState(pos);
        final LocksConfig.Lookup lookup = getBlockLookup(state.getBlock());
        if (lookup == null) return null;

        switch (lookup) {
            default:
            case single:
                return pos;
            case door:
                if (state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER)
                    return pos.down();
                else
                    return pos;
            case adjacent:
                return findBestAdjacentBlock(world, pos, state.getBlock());
        }
    }

    @Nullable
    private static LocksConfig.Lookup getBlockLookup(@Nonnull Block block) {
        return Misca.getSharedConfig().locks.tileless.get(block.getRegistryName());
    }

    private static BlockPos findBestAdjacentBlock(World world, BlockPos pos, Block block) {
        BlockPos bestPos = pos;
        for (EnumFacing e : EnumFacing.HORIZONTALS) {
            final BlockPos offPos = pos.offset(e);
            final IBlockState offState = world.getBlockState(pos.offset(e));
            if (offState.getBlock().equals(block) && offPos.toLong() < bestPos.toLong()) {
                bestPos = offPos;
            }
        }
        return bestPos;
    }
}
