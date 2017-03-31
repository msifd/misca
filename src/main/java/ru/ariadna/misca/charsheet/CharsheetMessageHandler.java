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

        switch (message.type) {
            case GET:
                String cs = CharsheetProvider.readCharsheet(message.payload);
                if (cs != null) {
                    CharsheetProvider.sendCharsheet(sender, cs);
                } else if (sender_name.equals(message.payload)) {
                    sender.addChatMessage(new ChatComponentTranslation("misca.charsheet.not_found_your"));
                } else {
                    sender.addChatMessage(new ChatComponentTranslation("misca.charsheet.not_found"));
                }
                break;
            case SET:
                CharsheetProvider.writeCharsheet(sender_name, message.payload);
                break;
            case REMOVE:
                CharsheetProvider.removeCharsheet(sender_name);
                break;
        }

        return null;
    }
}
