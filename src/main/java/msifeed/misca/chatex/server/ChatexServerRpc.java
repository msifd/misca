package msifeed.misca.chatex.server;

import msifeed.misca.Misca;
import msifeed.misca.chatex.IChatexRpc;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ChatexServerRpc implements IChatexRpc {
    @RpcMethodHandler(notifyTyping)
    public void onNotifyTyping(MessageContext ctx) {
        final EntityPlayerMP sender = ctx.getServerHandler().player;
        final long now = System.currentTimeMillis();
        Misca.RPC.sendToAllTracking(sender, broadcastTyping, sender.getEntityId(), now);
    }
}
