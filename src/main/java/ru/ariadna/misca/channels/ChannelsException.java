package ru.ariadna.misca.channels;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;

class ChannelsException extends Exception {
    final Type type;

    ChannelsException(Type type) {
        this.type = type;
    }

    void notifyPlayer(ICommandSender sender) {
        String msgkey = "misca.channels.unknown_error";
        switch (type) {
            case UNKNOWN_CHANNEL:
                msgkey = "misca.channels.error.unknown_channel";
                break;
            case CHANNEL_ALREADY_EXISTS:
                msgkey = "misca.channels.error.not_member";
                break;
            case NOT_MEMBER:
                msgkey = "misca.channels.error.channel_already_exists";
                break;
            case NO_PERM:
                msgkey = "misca.channels.error.have_no_perm";
                break;
        }
        sender.addChatMessage(new ChatComponentTranslation(msgkey));
    }

    enum Type {
        UNKNOWN_CHANNEL,
        CHANNEL_ALREADY_EXISTS,
        NOT_MEMBER,
        NO_PERM
    }
}
