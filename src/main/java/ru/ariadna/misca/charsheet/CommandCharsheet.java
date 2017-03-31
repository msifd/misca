package ru.ariadna.misca.charsheet;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class CommandCharsheet implements ICommand {
    private static final String HELP_MSG = "/charsheet [ init | preview | upload | clear | <player> ]";

    private SimpleNetworkWrapper network = new SimpleNetworkWrapper("misca.charsheet");

    CommandCharsheet() {
        network.registerMessage(CharsheetMessageHandler.class, CharsheetMessage.class, 0, Side.SERVER);
    }

    @Override
    public String getCommandName() {
        return "charsheet";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return HELP_MSG;
    }

    @Override
    public List getCommandAliases() {
        return null;
    }

    @Override
    public void processCommand(ICommandSender cmd_sender, String[] command_str) {
        EntityPlayer sender = (EntityPlayer) cmd_sender;
        String sender_name = sender.getDisplayName();

        if (command_str.length == 0) {
            // показать удаленный чаршит этого игрока
            requestCharsheet(sender_name);
        } else if (command_str.length == 1) {
            switch (command_str[0]) {
                case "init":
                    CharsheetProvider.initCharsheet(sender_name);
                    break;
                case "preview":
                    String cs = CharsheetProvider.readCharsheet(sender_name);
                    if (cs != null) {
                        CharsheetProvider.sendCharsheet(sender, cs);
                    } else {
                        sender.addChatMessage(new ChatComponentTranslation("misca.charsheet.not_found_your"));
                    }
                    break;
                case "upload":
                    String cs2 = CharsheetProvider.readCharsheet(sender_name);
                    if (cs2 != null) {
                        uploadCharsheet(cs2);
                    } else {
                        sender.addChatMessage(new ChatComponentTranslation("misca.charsheet.not_found_your"));
                    }
                    break;
                case "clear":
                    clearCharsheet();
                    break;
                default:
                    // показать удаленный чаршит указанного игрока
                    requestCharsheet(command_str[0]);
                    break;
            }
        } else {
            // хелп мессадж
            ChatComponentText cc = new ChatComponentText(HELP_MSG);
            ChatStyle cs = new ChatStyle();
            cs.setColor(EnumChatFormatting.RED);
            cc.setChatStyle(cs);
            sender.addChatMessage(cc);
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        if (sender instanceof EntityPlayer) {
            return true;
        } else {
            sender.addChatMessage(new ChatComponentTranslation("misca.charsheet.player_only"));
            return false;
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] command_str) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] command_str, int index) {
        String s = command_str[0];
        return index == 0 && !s.equals("init") && !s.equals("preview") && !s.equals("upload") && !s.equals("clear");
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

    private void clearCharsheet() {
        network.sendToServer(new CharsheetMessage(CharsheetMessage.Type.CLEAR, null));
    }
}
