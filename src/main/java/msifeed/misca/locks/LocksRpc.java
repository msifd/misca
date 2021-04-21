package msifeed.misca.locks;

import msifeed.misca.Misca;
import msifeed.misca.locks.cap.chunk.ChunkLockableProvider;
import msifeed.misca.locks.cap.chunk.IChunkLockable;
import msifeed.misca.locks.cap.lock.ILockable;
import msifeed.misca.locks.cap.lock.LockableProvider;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LocksRpc {
    private static final String tileLock = "locks.tile";
    private static final String chunkLocks = "locks.chunk";

    public static void syncTileLock(TileEntity tile, ILockable lock) {
        final BlockPos pos = tile.getPos();
        final NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(
                tile.getWorld().provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 0);
        Misca.RPC.sendToAllTracking(point, tileLock, pos, lock.getType().ordinal(), lock.getSecret());
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(tileLock)
    public void onTileLock(BlockPos pos, int typeOrd, int secret) {
        final World world = Minecraft.getMinecraft().world;
        final TileEntity tile = world.getTileEntity(pos);
        if (tile == null) return;

        final ILockable lock = LockableProvider.get(tile);
        if (lock == null) return;

        lock.setType(LockType.values()[typeOrd]);
        lock.setSecret(secret);
    }

    public static void sendChunkLocks(EntityPlayerMP player, Chunk chunk) {
        final IChunkLockable locks = ChunkLockableProvider.get(chunk);
        if (!locks.getLocks().isEmpty())
            Misca.RPC.sendTo(player, chunkLocks, chunk.x, chunk.z, ChunkLockableProvider.encode(locks));
    }

    public static void syncChunkLocks(Chunk chunk, IChunkLockable locks) {
        final NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(
                chunk.getWorld().provider.getDimension(), chunk.x * 16 + 8, 0,chunk.z * 16 + 8, 0);
        Misca.RPC.sendToAllTracking(point, chunkLocks, chunk.x, chunk.z, ChunkLockableProvider.encode(locks));
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(chunkLocks)
    public void onChunkLocks(int x, int z, NBTTagCompound nbt) {
        final World world = Minecraft.getMinecraft().world;
        final Chunk chunk = world.getChunkProvider().getLoadedChunk(x, z);
        if (chunk == null) return;

        final IChunkLockable chunkLocks = ChunkLockableProvider.decode(nbt);
        ChunkLockableProvider.get(chunk).replaceWith(chunkLocks);
        chunk.markDirty();
    }
}
