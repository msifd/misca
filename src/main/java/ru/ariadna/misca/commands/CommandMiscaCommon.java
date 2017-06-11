package ru.ariadna.misca.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import ru.ariadna.misca.config.ConfigManager;

public class CommandMiscaCommon extends CommandBase {
    @Override
    public String getCommandName() {
        return "misca";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/misca [reload]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.addChatMessage(new ChatComponentText("No arguments!"));
            return;
        }

        switch (args[0]) {
            case "reload":
                ConfigManager.instance.reloadConfig();
                sender.addChatMessage(new ChatComponentText("Misca reloaded"));
                break;
        }
    }
}
