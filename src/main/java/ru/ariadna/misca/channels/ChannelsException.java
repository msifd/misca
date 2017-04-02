package ru.ariadna.misca.channels;

import com.google.common.base.Throwables;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;

class ChannelsException extends Exception {
    private final Type type;

    ChannelsException(Type type) {
        this.type = type;
    }

    void notifyPlayer(ICommandSender sender) {
        String msgkey;
        switch (type) {
            case UNKNOWN_CHANNEL:
                msgkey = "misca.channels.error.unknown_channel";
                break;
            case NOT_MEMBER:
                msgkey = "misca.channels.error.not_member";
                break;
            case NO_PERM:
                msgkey = "misca.channels.error.have_no_perm";
                break;
            case PLAYER_ONLY:
                msgkey = "misca.command.player_only";
                break;
            default:
                msgkey = "misca.channels.error.unknown_error";
                ChatChannels.logger.error("Unknown error! {}", Throwables.getStackTraceAsString(new Throwable()));
                break;
        }
        sender.addChatMessage(new ChatComponentTranslation(msgkey));
    }

    enum Type {
        UNKNOWN_CHANNEL,
        NOT_MEMBER,
        NO_PERM,
        PLAYER_ONLY,
        UNKNOWN_PARAM
    }
}
