package msifeed.misca.chatex;

import msifeed.sys.rpc.RpcContext;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ChatexServerRpc implements IChatexRpc {
    @RpcMethodHandler(notifyTyping)
    public void onNotifyTyping(RpcContext ctx) {
        final EntityPlayerMP sender = ctx.getServerHandler().player;
        final long now = System.currentTimeMillis();
        ctx.rpc.sendToAllTracking(sender, broadcastTyping, sender.getEntityId(), now);
    }
}
