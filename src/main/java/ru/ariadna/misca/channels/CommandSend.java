package ru.ariadna.misca.channels;

import com.google.common.base.Joiner;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

import java.util.List;

public class CommandSend implements ICommand {
    private ChannelManager manager;

    CommandSend(ChannelManager manager) {
        this.manager = manager;
    }

    @Override
    public String getCommandName() {
        return "cc";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/cc <channel> <message>";
    }

    @Override
    public List getCommandAliases() {
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            return;
        }

        String ch = args[0];
        args[0] = null;
        try {
            manager.sendToChannel(ch, sender.getCommandSenderName(), Joiner.on(' ').skipNulls().join(args));
        } catch (ChannelsException e) {
            e.notifyPlayer(sender);
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return false;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return null;
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
