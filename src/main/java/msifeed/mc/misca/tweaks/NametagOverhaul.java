package msifeed.mc.misca.tweaks;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import msifeed.mc.misca.Misca;
import msifeed.mc.misca.tweaks.client.DistantNametagRender;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import java.io.*;
import java.util.HashSet;
import java.util.UUID;

public class NametagOverhaul implements IMessageHandler<NametagOverhaul.MessageNametag, IMessage> {
    private static final int MIN_NAMETAG_DISTANCE = 3;
    private static final int MIN_LOOKUP_DISTANCE = 10;

    private HashSet<UUID> hiddenOnes = new HashSet<>();
    private HashSet<UUID> publicOnes = new HashSet<>();
    private boolean lookupEnabled = false;

    private final CommandNametag commandNametag = new CommandNametag();
    private final CommandLookup commandLookup = new CommandLookup();

    public void initClient() {
        ClientCommandHandler.instance.registerCommand(commandLookup);
    }

    public void onServerStart(FMLServerStartingEvent event) {
        event.registerServerCommand(commandNametag);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderLivingSpecial(RenderLivingEvent.Specials.Pre event) {
        if (!(event.entity instanceof EntityPlayer)) return;

        final EntityPlayer self = Minecraft.getMinecraft().thePlayer;
        final EntityPlayer player = (EntityPlayer) event.entity;
        final UUID uuid = player.getUniqueID();

        final boolean incognito = hiddenOnes.contains(uuid);
        final float distance = self.getDistanceToEntity(player);
        final boolean hideNametag = distance > MIN_NAMETAG_DISTANCE;
        final boolean showLookup = distance > MIN_LOOKUP_DISTANCE && publicOnes.contains(uuid);

        if (lookupEnabled && showLookup && hideNametag && !incognito) DistantNametagRender.render(event, distance);
        if (hideNametag || incognito) event.setCanceled(true);
    }

    @SubscribeEvent
    @SideOnly(Side.SERVER)
    public void onPlayerJoin(EntityJoinWorldEvent event) {
        if (!(event.entity instanceof EntityPlayerMP)) return;

        final EntityPlayerMP player = (EntityPlayerMP) event.entity;
        final MessageNametag msg = new MessageNametag(this);
        Misca.tweaks.network.sendTo(msg, player);
    }

    @Override
    public IMessage onMessage(MessageNametag message, MessageContext ctx) {
        hiddenOnes.clear();
        hiddenOnes.addAll(message.hiddenOnes);
        publicOnes.clear();
        publicOnes.addAll(message.publicOnes);
        return null;
    }

    public class CommandNametag extends CommandBase {
        @Override
        public String getCommandName() {
            return "nametag";
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "Usage: /nametag [ | hide | public ]";
        }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if (!(sender instanceof EntityPlayer)) return;

            final String mode;
            if (args.length == 0) mode = "check";
            else mode = args[0];

            final UUID uuid = ((EntityPlayer) sender).getUniqueID();

            switch (mode) {
                case "check":
                    final String checkMsg = "Nametag: "
                            + (hiddenOnes.contains(uuid) ? "hidden " : "")
                            + (publicOnes.contains(uuid) ? "public " : "");
                    sender.addChatMessage(new ChatComponentText(checkMsg));
                    return;
                case "hide": {
                    final boolean enabling = !hiddenOnes.remove(uuid);
                    if (enabling) hiddenOnes.add(uuid);
                    sender.addChatMessage(new ChatComponentText(enabling ? "Nametag hidden" : "Nametag showed back"));
                }
                break;
                case "public": {
                    final boolean enabling = !publicOnes.remove(uuid);
                    if (enabling) publicOnes.add(uuid);
                    sender.addChatMessage(new ChatComponentText(enabling ? "Public nametag enabled" : "Public nametag disabled"));
                }
                break;
                default:
                    sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
                    return;
            }

            final MessageNametag msg = new MessageNametag(NametagOverhaul.this);
            Misca.tweaks.network.sendToAll(msg);
        }

        @Override
        public boolean canCommandSenderUseCommand(ICommandSender sender) {
            return sender instanceof EntityPlayer;
        }
    }

    public class CommandLookup extends CommandBase {
        @Override
        public String getCommandName() {
            return "lookup";
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "Usage: /lookup";
        }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            lookupEnabled = !lookupEnabled;
            sender.addChatMessage(new ChatComponentText(lookupEnabled ? "Lookup enabled" : "Lookup disabled"));
        }

        @Override
        public boolean canCommandSenderUseCommand(ICommandSender sender) {
            return sender instanceof EntityPlayer;
        }
    }


    public static class MessageNametag implements IMessage {
        HashSet<UUID> hiddenOnes = new HashSet<>();
        HashSet<UUID> publicOnes = new HashSet<>();

        public MessageNametag() {

        }

        public MessageNametag(NametagOverhaul module) {
            hiddenOnes.addAll(module.hiddenOnes);
            publicOnes.addAll(module.publicOnes);
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            int size = buf.readInt();
            ByteBuf map_buf = buf.readBytes(size);

            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(map_buf.array());
                ObjectInputStream ois = new ObjectInputStream(bis);
                this.hiddenOnes = (HashSet<UUID>) ois.readObject();
                this.publicOnes = (HashSet<UUID>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void toBytes(ByteBuf buf) {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(this.hiddenOnes);
                oos.writeObject(this.publicOnes);

                byte[] bytes = bos.toByteArray();
                buf.writeInt(bytes.length);
                buf.writeBytes(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
