package ru.ariadna.misca.channels;

import com.google.common.base.Joiner;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CommandChannel implements ICommand {
    private static final List<String> subcommands = Arrays.asList("help", "list", "register", "join", "invite", "leave", "remove", "set");

    private ChannelManager manager;

    CommandChannel(ChannelManager manager) {
        this.manager = manager;
    }

    @Override
    public String getCommandName() {
        return "channel";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/channel < help | list | register | join | leave | remove | set >";
    }

    @Override
    public List getCommandAliases() {
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sendManual(sender, null);
            return;
        }

        switch (args[0]) {
            case "list":
                Collection<Channel> chs = manager.listPlayerChannels(sender.getCommandSenderName());
                if (chs.isEmpty()) {
                    sender.addChatMessage(new ChatComponentTranslation("misca.channels.list.none"));
                } else {
                    List<String> chs_str = chs.stream().map(c -> c.name).collect(Collectors.toList());
                    String str = Joiner.on(' ').join(chs_str);
                    sender.addChatMessage(new ChatComponentTranslation("misca.channels.list.some", str));
                }
                break;
            case "register":
                try {
                    manager.registerChannel(sender.getCommandSenderName(), args[0]);
                } catch (ChannelsException e) {
                    e.notifyPlayer(sender);
                }
                break;
            case "join":
                try {
                    manager.joinToChannel(sender.getCommandSenderName(), args[0]);
                } catch (ChannelsException e) {
                    e.notifyPlayer(sender);
                }
                break;
            case "leave":
                try {
                    manager.leaveChannel(sender.getCommandSenderName(), args[0]);
                } catch (ChannelsException e) {
                    e.notifyPlayer(sender);
                }
                break;
            case "remove":
                try {
                    manager.removeChannel((EntityPlayer) sender, args[0]);
                } catch (ChannelsException e) {
                    e.notifyPlayer(sender);
                }
                break;
            case "set":
                if (args.length == 3) {
                    try {
                        manager.modifyChannel((EntityPlayer) sender, args[0], args[1], args[2]);
                    } catch (ChannelsException e) {
                        e.notifyPlayer(sender);
                    }
                } else {
                    sender.addChatMessage(new ChatComponentTranslation("misca.channels.help.set"));
                }
                break;
            case "help":
                sendManual(sender, null);
                break;
            default:
                // send "unknown command"
                break;
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        if (sender instanceof EntityPlayer) {
            return true;
        } else {
            sender.addChatMessage(new ChatComponentTranslation("misca.command.player_only"));
            return false;
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            return subcommands;
        } else if (args.length == 1) {
            return subcommands.stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] sender, int args) {
        return false;
    }

    @Override
    public int compareTo(Object cmp) {
        return this.getCommandName().compareTo(((ICommand) cmp).getCommandName());
    }

    private void sendManual(ICommandSender sender, String mode) {
        String help_msg = LanguageRegistry.instance().getStringLocalization("misca.channels.help");
        help_msg = StringEscapeUtils.unescapeJava(help_msg);
        sender.addChatMessage(new ChatComponentText(help_msg));
        // TODO send by line
    }
}
