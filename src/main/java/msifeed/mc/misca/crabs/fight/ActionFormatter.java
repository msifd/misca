package msifeed.mc.misca.crabs.fight;

import msifeed.mc.misca.crabs.character.Character;
import msifeed.mc.misca.crabs.character.Stats;
import msifeed.mc.misca.crabs.rules.ActionResult;
import msifeed.mc.misca.crabs.rules.DiceMath;
import msifeed.mc.misca.crabs.rules.FistFight;
import msifeed.mc.misca.utils.MiscaUtils;
import net.minecraft.entity.EntityLivingBase;

public final class ActionFormatter {
    public static String formatActionResults(ActionResult winner, ActionResult looser) {
        EntityLivingBase we = winner.ctx.entity, le = looser.ctx.entity;
        return "\u00A76[\u00A72"
                + we.getCommandSenderName()
                + "\u00A76>\u00A74"
                + le.getCommandSenderName()
                + "\u00A76] "
                + formatActionResult(winner)
                + " \u00A76> "
                + formatActionResult(looser);
    }

    public static String formatFatalityResult(EntityLivingBase winner, EntityLivingBase victim) {
        return "\u00A76[\u00A74FATALITY\u00A76] "
                + MiscaUtils.l10n("misca.crabs.finished", winner.getCommandSenderName(), victim.getCommandSenderName());
    }

    public static String formatActionResult(ActionResult action) {
        String s = "\u00A76"
                + action.action.pretty()
                + ": "
                + "["
                + diceRankColor(action.diceRank)
                + (action.totalSum - action.effectMod - action.playerMod);

        if (action.effectMod != 0) {
            s += "\u00A76"
                    + (action.effectMod > 0 ? "+" : "-")
                    + "\u00A7r"
                    + Math.abs(action.effectMod);
        }

        if (action.playerMod != 0) {
            s += "\u00A76"
                    + (action.playerMod > 0 ? "+" : "-")
                    + "\u00A7r"
                    + Math.abs(action.playerMod);
        }

        s += "\u00A76]\u00A7r";

        // Неуспешными помечаются только провалившиеся атакующие действия
        if (!action.actionSuccessful && !action.action.isDefencive())
            s += " " + MiscaUtils.l10n("misca.crabs.failed");

        return s;
    }

    public static String formatStatRoll(Character character, Stats stat, int roll, int mod) {
        final int statValue = character.stat(stat);
        String s = "\u00A76["
                + character.name
                + "] "
                + stat.toString()
                + ": ["
                + diceRankColor(DiceMath.DiceRank.ofD20(roll))
                + roll
                + "\u00A76]+\u00A7r"
                + statValue;

        if (mod != 0)
            s += (mod > 0 ? "\u00A76+\u00A7r" : "\u00A76-\u00A7r") + Math.abs(mod);

        s += "\u00A76=\u00A7r" + (roll + statValue + mod);

        return s;
    }

    public static String formatFistFightRoll(Character character, FistFight.Action action, int roll, int stats, int mod) {
        String s = "\u00A76["
                + character.name
                + "] "
                + action.pretty()
                + ": ["
                + diceRankColor(DiceMath.DiceRank.ofD20(roll))
                + roll
                + "\u00A76]+\u00A7r"
                + stats;

        if (mod != 0)
            s += (mod > 0 ? "\u00A76+\u00A7r" : "\u00A76-\u00A7r") + Math.abs(mod);

        s += "\u00A76=\u00A7r" + (roll + stats + mod);

        return s;
    }

    public static String diceRankColor(DiceMath.DiceRank rank) {
        switch (rank) {
            case LUCK:
                return "\u00A72";
            case FAIL:
                return "\u00A74";
            default:
                return "\u00A7r";
        }
    }
}
