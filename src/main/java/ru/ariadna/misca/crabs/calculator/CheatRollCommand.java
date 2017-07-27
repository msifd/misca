package ru.ariadna.misca.crabs.calculator;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import ru.ariadna.misca.crabs.characters.CharStats;
import ru.ariadna.misca.crabs.combat.parts.ActionType;

public class CheatRollCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "lawfulroll";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "lawfulroll <hit|shoot|def|magic|str|'STAT'> <dice> <stat> [mod]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) {
            sender.addChatMessage(new ChatComponentText("Only for players, sorry!"));
            return;
        }
        if (args.length < 3) {
            sender.addChatMessage(new ChatComponentText("Too few arguments!"));
            return;
        }

        String str_act = args[0].toUpperCase();
        ActionType action = null;
        CharStats charStat = null;
        switch (str_act) {
            case "HIT":
            case "SHOOT":
            case "MAGIC":
            case "DEFENCE":
                action = ActionType.valueOf(str_act);
                break;
            case "DEF":
                action = ActionType.DEFENCE;
                break;
            default:
                charStat = CharStats.fromShort(args[0]);
                if (charStat == null) {
                    sender.addChatMessage(new ChatComponentText("Unknown action! Valid: hit|shoot|def|defence|magic|'STAT'))"));
                    return;
                }
                break;
        }

        int dice;
        try {
            dice = Integer.valueOf(args[1]);
        } catch (NumberFormatException e) {
            sender.addChatMessage(new ChatComponentText("Invalid dice number!"));
            return;
        }

        int stat;
        try {
            stat = Integer.valueOf(args[2]);
        } catch (NumberFormatException e) {
            sender.addChatMessage(new ChatComponentText("Invalid stat number!"));
            return;
        }

        int mod = 0;
        if (args.length >= 4) {
            try {
                mod = Integer.valueOf(args[1]);
            } catch (NumberFormatException e) {
                sender.addChatMessage(new ChatComponentText("Invalid stat number!"));
                return;
            }
        }

        if (action != null) {
            CalcResult calcResult = new CalcResult(null);
            calcResult.action = action;
            calcResult.dice = dice;
            calcResult.stats = stat;
            calcResult.mod = mod;
            calcResult.result = dice + stat + mod;
            RollPrinter.sendActionRollResult((EntityPlayer) sender, calcResult);
        } else if (charStat != null) {
            RollPrinter.sendCustomRollResult((EntityPlayer) sender, charStat, dice, stat, mod);
        } else {
            sender.addChatMessage(new ChatComponentText("Invalid something!"));
        }
    }
}
