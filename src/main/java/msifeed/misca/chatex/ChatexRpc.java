package msifeed.misca.chatex;

import msifeed.misca.Misca;
import msifeed.misca.MiscaPerms;
import msifeed.misca.chatex.client.LogsSaver;
import msifeed.misca.chatex.client.TypingState;
import msifeed.misca.chatex.format.RollFormat;
import msifeed.misca.chatex.format.SpecialSpeechFormat;
import msifeed.misca.chatex.format.SpeechFormat;
import msifeed.misca.logdb.LogDB;
import msifeed.sys.rpc.RpcContext;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class ChatexRpc {
    private static final String cmd = "chatex.cmd";
    private static final String raw = "chatex.raw";
    private static final String rawPos = "chatex.rawPos";
    private static final String speech = "chatex.speech";
    private static final String speechPost = "chatex.speech.post";
    private static final String typing = "chatex.typing";
    private static final String typingPost = "chatex.typing.post";

    // // // // Server senders

    public static void broadcastSpeech(EntityPlayerMP sender, String msg) {
        final SpeechEvent event = new SpeechEvent(sender, msg, new TextComponentString(msg));
        if (MinecraftForge.EVENT_BUS.post(event)) return;

        Misca.RPC.sendToAllAround(sender, speech, sender.getUniqueID(), sender.getPosition(), sender.getDisplayNameString(), msg);
        LogDB.INSTANCE.log(sender, "speech", msg);
    }

    public static void sendGameMasterPM(EntityPlayerMP receiver, ITextComponent component) {
        Misca.RPC.sendTo(receiver, raw, component);
    }

    public static void broadcastGameMasterSay(EntityPlayerMP sender, int range, ITextComponent component) {
        Misca.RPC.sendToAllAround(sender, range, raw, component);
        LogDB.INSTANCE.log(sender, "raw", component);
    }

    public static void broadcastGameMasterGlobal(EntityPlayerMP sender, String msg) {
        final ITextComponent formatted = SpecialSpeechFormat.gmGlobal(sender, msg);
        for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
            if (MiscaPerms.isGameMaster(player))
                Misca.RPC.sendTo(player, raw, formatted);
        }
        LogDB.INSTANCE.log(sender, "gm-global", msg);
    }

    public static void broadcastGlobal(EntityPlayerMP sender, String msg) {
        Misca.RPC.sendToAll(raw, SpecialSpeechFormat.global(sender, msg));
        LogDB.INSTANCE.log(sender, "global", msg);
    }

    public static void broadcastOfftop(EntityPlayerMP sender, String msg) {
        final int range = Misca.getSharedConfig().chat.offtopRange;
        final ITextComponent formatted = SpecialSpeechFormat.offtop(sender, msg);
        Misca.RPC.sendToAllAround(sender, range, rawPos, sender.getPosition(), formatted);
        LogDB.INSTANCE.log(sender, "offtop", formatted);
    }

    public static void broadcastDiceRoll(EntityPlayerMP sender, String spec, long result) {
        final int range = Misca.getSharedConfig().chat.rollRange;
        final ITextComponent formatted = RollFormat.dice(sender, spec, result);
        Misca.RPC.sendToAllAround(sender, range, rawPos, sender.getPosition(), formatted);
        LogDB.INSTANCE.log(sender, "dice", formatted);
    }

    // // // // Server handlers

    @RpcMethodHandler(cmd)
    public void onCommand(RpcContext ctx, String command) {
        final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        server.getCommandManager().executeCommand(ctx.getServerHandler().player, command);
    }

    @RpcMethodHandler(speechPost)
    public void onSpeech(RpcContext ctx, String msg) {
        final EntityPlayerMP speaker = ctx.getServerHandler().player;
        if (GameMasterParams.INSTANCE.shouldUseGmSay(speaker)) {
            final GameMasterParams.Entry params = GameMasterParams.INSTANCE.getOrCreate(speaker.getUniqueID());
            broadcastGameMasterSay(speaker, params.range, params.format(msg));
        } else {
            broadcastSpeech(speaker, msg);
        }
    }

    @RpcMethodHandler(typingPost)
    public void onNotifyTyping(RpcContext ctx) {
        final EntityPlayerMP sender = ctx.getServerHandler().player;
        final long now = System.currentTimeMillis();
        Misca.RPC.sendToAllAround(sender, 16, typing, sender.getEntityId(), now);
    }

    // // // // Client senders

    public static void sendCommand(String msg) {
        Misca.RPC.sendToServer(cmd, msg);
    }

    public static void sendSpeech(String msg) {
        msg = net.minecraftforge.event.ForgeEventFactory.onClientSendMessage(msg);
        if (msg.isEmpty()) return;
        Misca.RPC.sendToServer(speechPost, msg);
    }

    public static void notifyTyping() {
        Misca.RPC.sendToServer(typingPost);
    }

    // // // // Client handlers

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(speech)
    public void onSpeechClient(UUID uuid, BlockPos pos, String name, String msg) {
        final EntityPlayerSP self = Minecraft.getMinecraft().player;

        final ITextComponent text = SpeechFormat.formatMessage(self, pos, msg).orElse(null);
        if (text == null) return;

        final ITextComponent fmtName = SpeechFormat.formatName(self, uuid, name);
        displayMessage(self, pos, SpeechFormat.join(fmtName, text));
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(raw)
    public void onRaw(ITextComponent component) {
        final EntityPlayerSP self = Minecraft.getMinecraft().player;
        displayMessage(self, self.getPosition(), component);
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(rawPos)
    public void onRawPos(BlockPos pos, ITextComponent component) {
        displayMessage(Minecraft.getMinecraft().player, pos, component);
    }

    @SideOnly(Side.CLIENT)
    private static void displayMessage(EntityPlayer self, BlockPos pos, ITextComponent tx) {
        self.sendMessage(tx);
        playNotificationSound(pos);
        LogsSaver.logSpeech(tx);
    }

    private static final SoundEvent chatSound = new SoundEvent(new ResourceLocation(Misca.MODID, "chatex.chat_message"));

    @SideOnly(Side.CLIENT)
    private static void playNotificationSound(BlockPos p) {
        final WorldClient w = FMLClientHandler.instance().getWorldClient();
        w.playSound(p.getX(), p.getY(), p.getZ(), chatSound, SoundCategory.PLAYERS, 1.0F, 0.7F, true);
    }

    // // // //

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(typing)
    public void onBroadcastTyping(int eid, long time) {
        TypingState.updateTyping(eid, time);
    }
}
