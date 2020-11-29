package msifeed.misca.locks.rpc;

import msifeed.misca.Misca;
import msifeed.misca.locks.chunk.ChunkLockableProvider;
import msifeed.misca.locks.chunk.IChunkLockable;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public interface ILockRpc {
    String chunkLocksRequest = "locks.chunk.req";
    String chunkLocks = "locks.chunk";

    static void requestChunkLocks(Chunk chunk) {
        Misca.RPC.sendToServer(chunkLocksRequest, chunk.x, chunk.z);
    }

    static void syncChunkLocks(Chunk chunk, IChunkLockable chunkLocks) {
        final NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(
                chunk.getWorld().provider.getDimension(), chunk.x * 16 + 8, 0,chunk.z * 16 + 8, 0);
        Misca.RPC.sendToAllTracking(point, ILockRpc.chunkLocks, chunk.x, chunk.z, ChunkLockableProvider.encode(chunkLocks));
    }
}
