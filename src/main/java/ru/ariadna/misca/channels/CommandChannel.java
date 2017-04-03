package ru.ariadna.misca.channels;

import com.google.common.base.Joiner;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

class CommandChannel implements ICommand {
    private static final List<String> subcommands = Arrays.asList("help", "list", "register", "join", "leave", "invite", "exclude", "remove", "set", "reload", "info");

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
        return "/channel < help | list | register | join | leave | invite | exclude | remove | set | reload | info >";
    }

    @Override
    public List getCommandAliases() {
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sendManual(sender, "help");
            return;
        }

        if (args.length == 1) {
            switch (args[0]) {
                case "list":
                    if (sender instanceof MinecraftServer) {
                        sender.addChatMessage(new ChatComponentTranslation("misca.channels.list.server"));
                        return;
                    }

                    Collection<String> chs = manager.listPlayerChannels(sender.getCommandSenderName());
                    if (chs.isEmpty()) {
                        sender.addChatMessage(new ChatComponentTranslation("misca.channels.list.none"));
                    } else {
                        String str = Joiner.on(' ').join(chs);
                        sender.addChatMessage(new ChatComponentTranslation("misca.channels.list.some", str));
                    }
                    return;
                case "reload":
                    try {
                        manager.reloadChannels(sender);
                        sender.addChatMessage(new ChatComponentTranslation("misca.channels.reload"));
                    } catch (ChannelsException e) {
                        e.notifyPlayer(sender);
                    }
                    return;
                default:
                    // Kinda universal Help command
                    sendManual(sender, args[0]);
                    return;
            }
        }

        switch (args[0]) {
            case "register":
                try {
                    manager.registerChannel(sender, args[1]);
                } catch (ChannelsException e) {
                    e.notifyPlayer(sender);
                }
                break;
            case "join":
                try {
                    manager.joinToChannel(sender, args[1]);
                } catch (ChannelsException e) {
                    e.notifyPlayer(sender);
                }
                break;
            case "invite":
                if (args.length == 3) {
                    try {
                        manager.inviteToChannel(sender, args[1], args[2]);
                    } catch (ChannelsException e) {
                        e.notifyPlayer(sender);
                    }
                } else {
                    sendManual(sender, args[0]);
                }
                break;
            case "leave":
                try {
                    manager.leaveChannel(sender, args[1]);
                } catch (ChannelsException e) {
                    e.notifyPlayer(sender);
                }
                break;
            case "exclude":
                if (args.length == 3) {
                    try {
                        manager.excludeFromChannel(sender, args[1], args[2]);
                    } catch (ChannelsException e) {
                        e.notifyPlayer(sender);
                    }
                } else {
                    sendManual(sender, args[0]);
                }
                break;
            case "remove":
                try {
                    manager.removeChannel(sender, args[1]);
                } catch (ChannelsException e) {
                    e.notifyPlayer(sender);
                }
                break;
            case "set":
                if (args.length == 4) {
                    try {
                        manager.modifyChannel(sender, args[1], args[2], args[3]);
                    } catch (ChannelsException e) {
                        e.notifyPlayer(sender);
                    }
                } else {
                    sendManual(sender, args[0]);
                }
                break;
            case "info":
                try {
                    manager.infoChannel(sender, args[1]);
                } catch (ChannelsException e) {
                    e.notifyPlayer(sender);
                }
                break;
            case "help":
                sendManual(sender, args[1]);
                break;
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return !manager.isSenderNotSuperuser(sender);
//        return true;
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
        String msg = LanguageRegistry.instance().getStringLocalization("misca.channels.help." + mode);
        msg = StringEscapeUtils.unescapeJava(msg);
        try {
            for (String line : IOUtils.readLines(new StringReader(msg))) {
                sender.addChatMessage(new ChatComponentText(line));
            }
        } catch (IOException e) {
            ChatChannels.logger.error("Unreachable! Tried to read help message by line.");
        }
    }
}
