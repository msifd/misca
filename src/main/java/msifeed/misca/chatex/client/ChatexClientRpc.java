package msifeed.misca.chatex.client;

import msifeed.misca.Misca;
import msifeed.misca.chatex.IChatexRpc;
import msifeed.misca.chatex.client.format.GlobalFormat;
import msifeed.misca.chatex.client.format.SpeechFormat;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Objects;
import java.util.UUID;

public class ChatexClientRpc implements IChatexRpc {
    private static final SoundEvent chatSound = new SoundEvent(new ResourceLocation(Misca.MODID, "chatex.chat_message"));

    @RpcMethodHandler(speech)
    public void onSpeech(UUID speakerId, int range, String msg) {
        final EntityPlayerSP self = Minecraft.getMinecraft().player;
        final EntityPlayer speaker = self.world.getPlayerEntityByUUID(speakerId);
        if (speaker == null) return;
        self.sendMessage(SpeechFormat.format(self, speaker, range, msg));
        playNotificationSound(speaker);
    }

    @RpcMethodHandler(global)
    public void onGlobal(String speaker, String msg) {
        final EntityPlayerSP self = Minecraft.getMinecraft().player;
        displaySelfMessage(self, GlobalFormat.format(self, speaker, msg));
    }

    @RpcMethodHandler(roll)
    public void onRoll(MessageContext ctx, UUID uuid, String spec, long result) {
        final NetworkPlayerInfo info = Objects.requireNonNull(ctx.getClientHandler().getPlayerInfo(uuid));
        final String name = info.getDisplayName() != null
                ? info.getDisplayName().getFormattedText()
                : info.getGameProfile().getName();
        final String msg = String.format("[ROLL] %s: %s = %d", name, spec, result);

        displaySelfMessage(Minecraft.getMinecraft().player, new TextComponentString(msg));
    }

    private static void displaySelfMessage(EntityPlayer self, ITextComponent tx) {
        self.sendMessage(tx);
        playNotificationSound(self);
    }

    private static void playNotificationSound(EntityPlayer p) {
        final WorldClient w = FMLClientHandler.instance().getWorldClient();
        w.playSound(p.posX, p.posY, p.posZ, chatSound, SoundCategory.PLAYERS, 1.0F, 0.7F, true);
    }

    // // // //

    @RpcMethodHandler(broadcastTyping)
    public void onBroadcastTyping(int entityId, long time) {
        TypingState.updateTyping(entityId, time);
    }
}
