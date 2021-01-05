package msifeed.misca.chatex;

import msifeed.misca.Misca;
import msifeed.misca.chatex.client.TypingState;
import msifeed.misca.chatex.client.format.GlobalFormat;
import msifeed.misca.chatex.client.format.SpeechFormat;
import msifeed.sys.rpc.RpcContext;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;
import java.util.UUID;

public class ChatexRpc {
    private static final String speech = "chatex.speech";
    private static final String global = "chatex.global";
    private static final String roll = "chatex.roll";
    private static final String notifyTyping = "chatex.typing.notify";
    private static final String broadcastTyping = "chatex.typing.broadcast";

    private static final SoundEvent chatSound = new SoundEvent(new ResourceLocation(Misca.MODID, "chatex.chat_message"));

    // // // // Shared handlers

    @RpcMethodHandler(speech)
    public void onSpeech(RpcContext ctx, UUID speakerId, int range, String msg) {
        if (ctx.side.isServer()) {
            final EntityPlayerMP sender = ctx.getServerHandler().player;
            final EntityPlayer speaker = sender.world.getPlayerEntityByUUID(speakerId);
            if (speaker instanceof EntityPlayerMP)
                broadcastSpeech((EntityPlayerMP) speaker, range, msg);
        } else {
            onSpeechClient(speakerId, range, msg);
        }
    }

    @SideOnly(Side.CLIENT)
    private void onSpeechClient(UUID speakerId, int range, String msg) {
        final EntityPlayerSP self = Minecraft.getMinecraft().player;
        final EntityPlayer speaker = self.world.getPlayerEntityByUUID(speakerId);
        if (speaker == null) return;
        self.sendMessage(SpeechFormat.format(self, speaker, range, msg));
        playNotificationSound(speaker);
    }

    // // // // Server senders

    public static void broadcastSpeech(EntityPlayerMP player, int range, String msg) {
        Misca.RPC.sendToAllTracking(player, speech, player.getUniqueID(), range, msg);
        Misca.RPC.sendTo(player, speech, player.getUniqueID(), range, msg);
    }

    public static void broadcastGlobal(EntityPlayerMP player, String msg) {
        // TODO: get name from charsheet
        Misca.RPC.sendToAll(global, player.getDisplayNameString(), msg);
    }

    public static void broadcastRoll(EntityPlayerMP sender, String spec, long result) {
        Misca.RPC.sendToAllTracking(sender, roll, sender.getUniqueID(), spec, result);
        Misca.RPC.sendTo(sender, roll, sender.getUniqueID(), spec, result);
    }

    public static void notifyTyping() {
        Misca.RPC.sendToServer(notifyTyping);
    }

    // // // // Server handlers

    @RpcMethodHandler(notifyTyping)
    public void onNotifyTyping(RpcContext ctx) {
        final EntityPlayerMP sender = ctx.getServerHandler().player;
        final long now = System.currentTimeMillis();
        Misca.RPC.sendToAllTracking(sender, broadcastTyping, sender.getEntityId(), now);
    }

    // // // // Client senders

    public static void sendSpeech(UUID speakerId, int range, String msg) {
        Misca.RPC.sendToServer(speech, speakerId, range, msg);
    }

    // // // // Client handlers

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(global)
    public void onGlobal(String speaker, String msg) {
        final EntityPlayerSP self = Minecraft.getMinecraft().player;
        displaySelfMessage(self, GlobalFormat.format(self, speaker, msg));
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(roll)
    public void onRoll(RpcContext ctx, UUID uuid, String spec, long result) {
        final NetworkPlayerInfo info = Objects.requireNonNull(ctx.getClientHandler().getPlayerInfo(uuid));
        final String name = info.getDisplayName() != null
                ? info.getDisplayName().getFormattedText()
                : info.getGameProfile().getName();
        final String msg = String.format("[ROLL] %s: %s = %d", name, spec, result);

        displaySelfMessage(Minecraft.getMinecraft().player, new TextComponentString(msg));
    }

    @SideOnly(Side.CLIENT)
    private static void displaySelfMessage(EntityPlayer self, ITextComponent tx) {
        self.sendMessage(tx);
        playNotificationSound(self);
    }

    @SideOnly(Side.CLIENT)
    private static void playNotificationSound(EntityPlayer p) {
        final WorldClient w = FMLClientHandler.instance().getWorldClient();
        w.playSound(p.posX, p.posY, p.posZ, chatSound, SoundCategory.PLAYERS, 1.0F, 0.7F, true);
    }

    // // // //

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(broadcastTyping)
    public void onBroadcastTyping(int entityId, long time) {
        TypingState.updateTyping(entityId, time);
    }
}
