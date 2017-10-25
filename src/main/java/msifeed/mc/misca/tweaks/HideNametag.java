package msifeed.mc.misca.tweaks;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import msifeed.mc.misca.Misca;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import java.io.*;
import java.util.HashSet;

/**
 * Отменяет рендеринг плашки с именем
 */
public class HideNametag implements IMessageHandler<HideNametag.MessageIncognito, IMessage> {
    private static final int RENDER_DISTANCE = 3;
    CommandIncognito commandIncognito = new CommandIncognito(this);
    private HashSet<String> incognitos = new HashSet<>();

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderLivingSpecial(RenderLivingEvent.Specials.Pre event) {
        EntityPlayer self = Minecraft.getMinecraft().thePlayer;
        if (event.entity instanceof EntityPlayer && event.isCancelable()
                && (self.getDistanceToEntity(event.entity) > RENDER_DISTANCE
                || incognitos.contains(event.entity.getCommandSenderName()))) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.SERVER)
    public void onPlayerJoin(EntityJoinWorldEvent event) {
        if (!(event.entity instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.entity;
        MessageIncognito msg = new MessageIncognito();
        msg.incognitos.addAll(incognitos);
        Misca.tweaks.network.sendTo(msg, player);
    }

    @Override
    public IMessage onMessage(MessageIncognito message, MessageContext ctx) {
        incognitos.clear();
        incognitos.addAll(message.incognitos);
        return null;
    }

    public static class MessageIncognito implements IMessage {
        HashSet<String> incognitos = new HashSet<>();

        @Override
        public void fromBytes(ByteBuf buf) {
            int size = buf.readInt();
            ByteBuf map_buf = buf.readBytes(size);

            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(map_buf.array());
                ObjectInputStream ois = new ObjectInputStream(bis);
                this.incognitos = (HashSet<String>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void toBytes(ByteBuf buf) {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(this.incognitos);

                byte[] bytes = bos.toByteArray();
                buf.writeInt(bytes.length);
                buf.writeBytes(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class CommandIncognito extends CommandBase {
        private HideNametag module;

        CommandIncognito(HideNametag module) {
            this.module = module;
        }

        @Override
        public String getCommandName() {
            return "incognito";
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "/incognito";
        }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            String name = sender.getCommandSenderName();
            boolean enabling = !module.incognitos.remove(name);
            if (enabling)
                module.incognitos.add(name);
            MessageIncognito msg = new MessageIncognito();
            msg.incognitos.addAll(module.incognitos);
            Misca.tweaks.network.sendToAll(msg);
            sender.addChatMessage(new ChatComponentText(enabling ? "Incognito enabled" : "Incognito disabled"));
        }

        @Override
        public boolean canCommandSenderUseCommand(ICommandSender sender) {
            return sender instanceof EntityPlayer;
        }
    }
}
