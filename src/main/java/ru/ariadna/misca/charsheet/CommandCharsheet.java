package ru.ariadna.misca.charsheet;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandCharsheet implements ICommand {
    private static final List<String> subcommands = Arrays.asList("help", "preInit", "preview", "upload", "remove");

    private SimpleNetworkWrapper network = new SimpleNetworkWrapper("misca.charsheet");
    private CharsheetProvider provider;

    CommandCharsheet(CharsheetProvider provider) {
        this.provider = provider;
        network.registerMessage(CharsheetMessageHandler.class, CharsheetMessage.class, 0, Side.SERVER);
    }

    @Override
    public String getCommandName() {
        return "charsheet";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/charsheets help";
    }

    @Override
    public List getCommandAliases() {
        return null;
    }

    @Override
    public void processCommand(ICommandSender cmd_sender, String[] args) {
        EntityPlayer sender = (EntityPlayer) cmd_sender;
        String sender_name = sender.getDisplayName();

        if (args.length == 0) {
            // показать удаленный чаршит этого игрока
            requestCharsheet(sender_name);
            return;
        }

        switch (args[0]) {
            case "preInit":
                provider.initCharsheet(sender_name);
                break;
            case "preview":
                String cs = provider.readCharsheet(sender_name);
                if (cs != null) {
                    provider.sendCharsheet(sender, sender_name, cs);
                } else {
                    sender.addChatMessage(new ChatComponentTranslation("misca.charsheet.not_found_your"));
                }
                break;
            case "upload":
                String cs2 = provider.readCharsheet(sender_name);
                if (cs2 != null) {
                    uploadCharsheet(cs2);
                } else {
                    sender.addChatMessage(new ChatComponentTranslation("misca.charsheet.not_found_your"));
                }
                break;
            case "remove":
                removeCharsheet();
                break;
            case "help":
                String msg = LanguageRegistry.instance().getStringLocalization("misca.charsheet.help");
                msg = StringEscapeUtils.unescapeJava(msg);
                try {
                    for (String line : IOUtils.readLines(new StringReader(msg))) {
                        sender.addChatMessage(new ChatComponentText(line));
                    }
                } catch (IOException e) {
                    Charsheets.logger.error("Unreachable! Tried to read charsheet help by line.");
                }
                break;
            default:
                // показать удаленный чаршит указанного игрока
                requestCharsheet(args[0]);
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
        if (args.length == 1) {
            return subcommands.stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        }
        return subcommands;
    }

    @Override
    public boolean isUsernameIndex(String[] command_str, int index) {
        String s = command_str[0];
        return index == 0 && !subcommands.contains(s);
    }

    @Override
    public int compareTo(Object cmp) {
        ICommand cmd = (ICommand) cmp;
        return this.getCommandName().compareTo(cmd.getCommandName());
    }

    private void requestCharsheet(String username) {
        network.sendToServer(new CharsheetMessage(CharsheetMessage.Type.GET, username));
    }

    private void uploadCharsheet(String text) {
        network.sendToServer(new CharsheetMessage(CharsheetMessage.Type.SET, text));
    }

    private void removeCharsheet() {
        network.sendToServer(new CharsheetMessage(CharsheetMessage.Type.REMOVE, null));
    }
}
