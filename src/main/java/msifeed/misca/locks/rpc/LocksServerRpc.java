package msifeed.misca.locks.rpc;

import msifeed.misca.Misca;
import msifeed.misca.locks.chunk.ChunkLockableProvider;
import msifeed.misca.locks.chunk.IChunkLockable;
import msifeed.sys.rpc.RpcContext;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.chunk.Chunk;

public class LocksServerRpc implements ILockRpc {
    @RpcMethodHandler(chunkLocksRequest)
    public void onChunkLocksRequest(RpcContext ctx, int x, int z) {
        final EntityPlayerMP player = ctx.getServerHandler().player;
        final Chunk chunk = player.world.getChunkProvider().getLoadedChunk(x, z);
        if (chunk == null) return;

        final IChunkLockable chunkLocks = ChunkLockableProvider.get(chunk);
        if (!chunkLocks.getLocks().isEmpty())
            Misca.RPC.sendTo(player, ILockRpc.chunkLocks, x, z, ChunkLockableProvider.encode(chunkLocks));
    }
}
