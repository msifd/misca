package msifeed.misca.chatex.client;

import msifeed.misca.chatex.IChatexRpc;
import msifeed.misca.chatex.client.format.GlobalFormat;
import msifeed.misca.chatex.client.format.SpeechFormat;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Objects;
import java.util.UUID;

public class ChatexClientRpc implements IChatexRpc {
    @RpcMethodHandler(speech)
    public void onSpeech(MessageContext ctx, UUID speakerId, int range, String msg) {
        final EntityPlayerSP self = Minecraft.getMinecraft().player;
        final EntityPlayer speaker = self.world.getPlayerEntityByUUID(speakerId);
        if (speaker == null) return;
        self.sendMessage(SpeechFormat.format(self, speaker, range, msg));
    }

    @RpcMethodHandler(global)
    public void onGlobal(MessageContext ctx, String speaker, String msg) {
        final EntityPlayerSP self = Minecraft.getMinecraft().player;
        self.sendMessage(GlobalFormat.format(self, speaker, msg));
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
