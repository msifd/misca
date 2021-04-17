package msifeed.misca.chatex;

import msifeed.misca.Misca;
import msifeed.misca.charsheet.CharEffort;
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
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;
import java.util.UUID;

public class ChatexRpc {
    private static final String cmd = "chatex.cmd";
    private static final String raw = "chatex.raw";
    private static final String gmGlobal = "chatex.gmGlobal";
    private static final String speech = "chatex.speech";
    private static final String global = "chatex.global";
    private static final String offtop = "chatex.offtop";
    private static final String diceRoll = "chatex.diceRoll";
    private static final String effortRoll = "chatex.effortRoll";
    private static final String notifyTyping = "chatex.typing.notify";
    private static final String broadcastTyping = "chatex.typing.broadcast";

    // // // // Shared handlers

    @RpcMethodHandler(speech)
    public void onSpeech(RpcContext ctx, UUID speakerId, String msg) {
        if (ctx.side.isServer()) {
            final EntityPlayerMP sender = ctx.getServerHandler().player;
            final EntityPlayerMP speaker = (EntityPlayerMP) sender.world.getPlayerEntityByUUID(speakerId);

            if (sender == speaker && GameMasterParams.INSTANCE.shouldUseGmSay(speaker)) {
                final GameMasterParams.Entry params = GameMasterParams.INSTANCE.getOrCreate(speaker.getUniqueID());
                broadcastRaw(speaker, params.range, params.format(msg));
            } else {
                broadcastSpeech(speaker, msg);
            }
        } else {
            onSpeechClient(speakerId, msg);
        }
    }

    // // // // Server senders

    public static void directRaw(EntityPlayerMP receiver, ITextComponent component) {
        Misca.RPC.sendTo(receiver, raw, component);
    }

    public static void broadcastRaw(EntityPlayerMP sender, int range, ITextComponent component) {
        Misca.RPC.sendToAllAround(sender, range, raw, component);
        LogDB.INSTANCE.log(sender, "raw", component);
    }

    public static void broadcastSpeech(EntityPlayerMP sender, String msg) {
        final SpeechEvent event = new SpeechEvent(sender, msg, new TextComponentString(msg));
        if (MinecraftForge.EVENT_BUS.post(event)) return;

        Misca.RPC.sendToAllVisibleTo(sender, speech, sender.getUniqueID(), msg);
        LogDB.INSTANCE.log(sender, "speech", msg);
    }

    public static void broadcastGameMasterGlobal(EntityPlayerMP sender, String msg) {
        // TODO: get name from charsheet
        Misca.RPC.sendToAll(gmGlobal, sender.getName(), msg);
        LogDB.INSTANCE.log(sender, "gm-global", msg);
    }

    public static void broadcastGlobal(EntityPlayerMP sender, String msg) {
        // TODO: get name from charsheet
        Misca.RPC.sendToAll(global, sender.getDisplayNameString(), msg);
        LogDB.INSTANCE.log(sender, "global", msg);
    }

    public static void broadcastOfftop(EntityPlayerMP sender, String msg) {
        final int range = Misca.getSharedConfig().chat.offtopRange;
        Misca.RPC.sendToAllAround(sender, range, offtop, sender.getUniqueID(), msg);
        LogDB.INSTANCE.log(sender, "offtop", SpecialSpeechFormat.offtop(sender, msg));
    }

    public static void broadcastDiceRoll(EntityPlayerMP sender, String spec, long result) {
        final int range = Misca.getSharedConfig().chat.rollRange;
        Misca.RPC.sendToAllAround(sender, range, diceRoll, sender.getUniqueID(), spec, result);
        LogDB.INSTANCE.log(sender, "dice", RollFormat.dice(sender.getDisplayNameString(), spec, result));
    }

    public static void broadcastEffortRoll(EntityPlayerMP sender, CharEffort effort, int amount, int difficulty, boolean result) {
        final int range = Misca.getSharedConfig().chat.rollRange;
        Misca.RPC.sendToAllAround(sender, range, effortRoll, sender.getUniqueID(), effort.ordinal(), amount, difficulty, result);
        LogDB.INSTANCE.log(sender, "effort", RollFormat.effort(sender, effort, amount, difficulty, result));
    }

    public static void notifyTyping() {
        Misca.RPC.sendToServer(notifyTyping);
    }

    // // // // Server handlers

    @RpcMethodHandler(cmd)
    public void onCommand(RpcContext ctx, String command) {
        final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        server.getCommandManager().executeCommand(ctx.getServerHandler().player, command);
    }

    @RpcMethodHandler(notifyTyping)
    public void onNotifyTyping(RpcContext ctx) {
        final EntityPlayerMP sender = ctx.getServerHandler().player;
        final long now = System.currentTimeMillis();
        Misca.RPC.sendToAllAround(sender, 16, broadcastTyping, sender.getEntityId(), now);
    }

    // // // // Client senders

    public static void sendCommand(String msg) {
        Misca.RPC.sendToServer(cmd, msg);
    }

    public static void sendSpeech(UUID speakerId, String msg) {
        msg = net.minecraftforge.event.ForgeEventFactory.onClientSendMessage(msg);
        if (msg.isEmpty()) return;
        Misca.RPC.sendToServer(speech, speakerId, msg);
    }

    // // // // Client handlers

    @SideOnly(Side.CLIENT)
    private void onSpeechClient(UUID speakerId, String msg) {
        final EntityPlayerSP self = Minecraft.getMinecraft().player;
        final EntityPlayer speaker = self.world.getPlayerEntityByUUID(speakerId);
        if (speaker == null) return;

        SpeechFormat.format(self, speaker, msg)
                .ifPresent(text -> displayMessage(self, speaker, text));
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(raw)
    public void onRaw(ITextComponent component) {
        final EntityPlayerSP self = Minecraft.getMinecraft().player;
        displayMessage(self, self, component);
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(gmGlobal)
    public void onGmGlobal(String speaker, String msg) {
        final EntityPlayerSP self = Minecraft.getMinecraft().player;
        displayMessage(self, self, SpecialSpeechFormat.gmGlobal(self, speaker, msg));
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(global)
    public void onGlobal(String speaker, String msg) {
        final EntityPlayerSP self = Minecraft.getMinecraft().player;
        displayMessage(self, self, SpecialSpeechFormat.global(self, speaker, msg));
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(offtop)
    public void onOfftop(UUID uuid, String msg) {
        final EntityPlayer sender = Minecraft.getMinecraft().world.getPlayerEntityByUUID(uuid);
        if (sender == null) return;

        final EntityPlayerSP self = Minecraft.getMinecraft().player;
        displayMessage(self, self, SpecialSpeechFormat.offtop(sender, msg));
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(diceRoll)
    public void onDiceRoll(RpcContext ctx, UUID uuid, String spec, long result) {
        final NetworkPlayerInfo info = Objects.requireNonNull(ctx.getClientHandler().getPlayerInfo(uuid));
        final String name = info.getDisplayName() != null
                ? info.getDisplayName().getFormattedText()
                : info.getGameProfile().getName();

        final EntityPlayer self = Minecraft.getMinecraft().player;
        displayMessage(self, self, RollFormat.dice(name, spec, result));
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(effortRoll)
    public void onEffortRoll(RpcContext ctx, UUID uuid, int effortOrd, int amount, int difficulty, boolean result) {
        final EntityPlayer target = Minecraft.getMinecraft().world.getPlayerEntityByUUID(uuid);
        if (target == null) return;

        final CharEffort effort = CharEffort.values()[effortOrd];
        displayMessage(Minecraft.getMinecraft().player, target, RollFormat.effort(target, effort, amount, difficulty, result));
    }

    @SideOnly(Side.CLIENT)
    private static void displayMessage(EntityPlayer self, EntityPlayer source, ITextComponent tx) {
        self.sendMessage(tx);
        playNotificationSound(source);
        LogsSaver.logSpeech(tx);
    }

    private static final SoundEvent chatSound = new SoundEvent(new ResourceLocation(Misca.MODID, "chatex.chat_message"));

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
