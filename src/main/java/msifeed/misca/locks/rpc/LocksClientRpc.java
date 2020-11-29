package msifeed.misca.locks.rpc;

import msifeed.misca.locks.chunk.ChunkLockableProvider;
import msifeed.misca.locks.chunk.IChunkLockable;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class LocksClientRpc implements ILockRpc {
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
