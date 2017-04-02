package ru.ariadna.misca.charsheet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;

public class CharsheetMessageHandler implements IMessageHandler<CharsheetMessage, IMessage> {

    @Override
    public IMessage onMessage(CharsheetMessage message, MessageContext ctx) {
        EntityPlayerMP sender = ctx.getServerHandler().playerEntity;
        String sender_name = sender.getDisplayName();
        CharsheetProvider provider = Charsheets.provider;

        switch (message.type) {
            case GET:
                String cs = provider.readCharsheet(message.payload);
                if (cs != null) {
                    provider.sendCharsheet(sender, message.payload, cs);
                } else if (sender_name.equals(message.payload)) {
                    sender.addChatMessage(new ChatComponentTranslation("misca.charsheet.not_found_your"));
                } else {
                    sender.addChatMessage(new ChatComponentTranslation("misca.charsheet.not_found"));
                }
                break;
            case SET:
                provider.writeCharsheet(sender_name, message.payload);
                sender.addChatMessage(new ChatComponentTranslation("misca.charsheet.upload"));
                break;
            case REMOVE:
                provider.removeCharsheet(sender_name);
                sender.addChatMessage(new ChatComponentTranslation("misca.charsheet.remove"));
                break;
        }

        return null;
    }
}
