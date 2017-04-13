package ru.ariadna.misca.combat.commands;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import ru.ariadna.misca.MiscaUtils;
import ru.ariadna.misca.combat.calculation.CalcRulesProvider;
import ru.ariadna.misca.combat.characters.CharacterProvider;

import java.util.List;

public class CommandMiscaCombat implements ICommand {
    private final CharacterProvider charProvider;
    private final CalcRulesProvider ruleProvider;

    public CommandMiscaCombat(CharacterProvider charProvider, CalcRulesProvider ruleProvider) {
        this.charProvider = charProvider;
        this.ruleProvider = ruleProvider;
    }

    @Override
    public String getCommandName() {
        return "misca-combat";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/misca-combat [reload-chars|reload-rules]";
    }

    @Override
    public List getCommandAliases() {
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            return;
        }

        switch (args[0].toLowerCase()) {
            case "reload-chars":
                charProvider.reloadCharacters();
                sender.addChatMessage(new ChatComponentText("Characters reloaded"));
                break;
            case "reload-rules":
                boolean res = ruleProvider.reload();
                sender.addChatMessage(new ChatComponentText(res ? "Rules reloaded" : "Rule reload failed! Check log."));
                break;
            default:
                sender.addChatMessage(new ChatComponentText("No such argument"));
                break;
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return MiscaUtils.isSuperuser(sender);
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] sender, int index) {
        return false;
    }

    @Override
    public int compareTo(Object cmp) {
        return this.getCommandName().compareTo(((ICommand) cmp).getCommandName());
    }
}
