package msifeed.misca.chatex.client;

import msifeed.misca.chatex.IChatexRpc;
import msifeed.misca.rpc.RpcMethodHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Objects;
import java.util.UUID;

public class ChatexClientRpc implements IChatexRpc {
    @RpcMethodHandler(speech)
    public void onSpeech(MessageContext ctx, int entityId, ITextComponent msg) {
        final EntityPlayerSP self = Minecraft.getMinecraft().player;
        self.sendMessage(msg);
    }

    @RpcMethodHandler(global)
    public void onGlobal(MessageContext ctx, int entityId, ITextComponent msg) {
        final EntityPlayerSP self = Minecraft.getMinecraft().player;
        self.sendMessage(msg);
    }

    @RpcMethodHandler(roll)
    public void onRoll(MessageContext ctx, UUID uuid, String spec, long result) {
        final NetworkPlayerInfo info = Objects.requireNonNull(ctx.getClientHandler().getPlayerInfo(uuid));
        final String name = info.getDisplayName() != null
                ? info.getDisplayName().getFormattedText()
                : info.getGameProfile().getName();
        final String msg = String.format("[ROLL] %s: %s = %d", name, spec, result);

        final EntityPlayerSP self = Minecraft.getMinecraft().player;
        self.sendMessage(new TextComponentString(msg));
    }

    // // // //

    @RpcMethodHandler(broadcastTyping)
    public void onBroadcastTyping(MessageContext ctx, int entityId, long time) {
        TypingHandler.updateTyping(entityId, time);
    }
}
