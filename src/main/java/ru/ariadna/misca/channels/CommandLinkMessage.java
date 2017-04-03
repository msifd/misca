package ru.ariadna.misca.channels;

import com.google.common.base.Joiner;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

class CommandLinkMessage implements ICommand {
    private ChannelManager manager;

    CommandLinkMessage(ChannelManager manager) {
        this.manager = manager;
    }

    @Override
    public String getCommandName() {
        return "cl";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/cl <link> <player> <message>";
    }

    @Override
    public List getCommandAliases() {
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.addChatMessage(new ChatComponentTranslation("misca.channels.cl.help"));
            return;
        }

        String channel = args[0];
        String player = args[1];
        args[0] = null;
        args[1] = null;
        try {
            manager.sendToLink(sender, channel, player, Joiner.on(' ').skipNulls().join(args));
        } catch (ChannelsException e) {
            e.notifyPlayer(sender);
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length != 1) {
            return null;
        }
        Collection<String> chs = manager.listPlayerChannels(sender.getCommandSenderName());
        if (chs.isEmpty()) {
            return null;
        }
        return chs.stream().filter(cs -> cs.startsWith(args[0])).collect(Collectors.toList());
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(Object cmp) {
        return this.getCommandName().compareTo(((ICommand) cmp).getCommandName());
    }

}
