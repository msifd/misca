package ru.ariadna.misca.combat.commands;

import com.google.common.base.Joiner;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import ru.ariadna.misca.combat.Combat;
import ru.ariadna.misca.combat.calculation.CalcResult;
import ru.ariadna.misca.combat.calculation.CalcRule;
import ru.ariadna.misca.combat.calculation.Calculon;
import ru.ariadna.misca.combat.characters.Character;
import ru.ariadna.misca.combat.characters.CharacterProvider;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class CommandDice implements ICommand {
    private final CharacterProvider provider;

    public CommandDice(CharacterProvider provider) {
        this.provider = provider;
    }

    @Override
    public String getCommandName() {
        return "dice";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/dice [dices] [stat*coefficient] [modifier] ; /dice help";
    }

    @Override
    public List getCommandAliases() {
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            String msg = LanguageRegistry.instance().getStringLocalization("misca.combat.dice.help");
            msg = StringEscapeUtils.unescapeJava(msg);
            try {
                for (String line : IOUtils.readLines(new StringReader(msg))) {
                    sender.addChatMessage(new ChatComponentText(line));
                }
            } catch (IOException e) {
                Combat.logger.error("Unreachable! Tried to read dice help by line.");
            }
            return;
        }

        String rule_raw = Joiner.on(' ').join(args);
        try {
            CalcRule rule = new CalcRule(rule_raw);
            Character character = provider.get(sender.getCommandSenderName());
            CalcResult result = Calculon.calculate(character, rule);

            if (character.name.isEmpty())
                sender.addChatMessage(new ChatComponentTranslation("misca.combat.dice.default_char"));
            sender.addChatMessage(new ChatComponentText(result.toString()));
        } catch (CalcRule.RuleParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayer;
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
