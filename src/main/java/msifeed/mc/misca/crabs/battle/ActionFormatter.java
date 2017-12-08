package msifeed.mc.misca.crabs.battle;

import msifeed.mc.misca.crabs.character.Stats;
import msifeed.mc.misca.crabs.rules.ActionResult;
import msifeed.mc.misca.crabs.rules.DiceMath;
import net.minecraft.entity.EntityLivingBase;

public final class ActionFormatter {
    public static String formatActionResults(ActionResult winner, ActionResult looser) {
        EntityLivingBase we = winner.ctx.entity, le = looser.ctx.entity;
        return "\u00A76[\u00A72"
                + we.getCommandSenderName()
                + "\u00A76>\u00A74"
                + le.getCommandSenderName()
                + "\u00A76]:\u00A7r "
                + formatAction(winner)
                + " \u00A76>\u00A7r "
                + formatAction(looser);
    }

    public static String formatAction(ActionResult action) {
        String s = "\u00A76"
                + action.action.name
                + ": "
                + "["
                + diceRankColor(action.diceRank)
                + action.diceSum
                + "\u00A76]+\u00A7r"
                + action.statSum;

        if (action.modSum != 0)
            s += action.modSum > 0 ? "\u00A76+\u00A7r" : "\u00A76-\u00A7r" + Math.abs(action.modSum);
        if (action.playerMod != 0)
            s += action.playerMod > 0 ? "\u00A76+\u00A7r" : "\u00A76-\u00A7r" + Math.abs(action.playerMod);

        s += "\u00A76=" + diceRankColor(action.diceRank) + action.totalSum;

        return s;
    }

    public static String formatStatRoll(Stats stat, int roll, int statValue, int mod) {
        String s = "\u00A76["
                + stat.toString()
                + "]: ["
                + diceRankColor(DiceMath.DiceRank.of(roll))
                + roll
                + "\u00A76]+\u00A7r"
                + statValue;

        if (mod != 0)
            s += mod > 0 ? "\u00A76+\u00A7r" : "\u00A76-\u00A7r" + Math.abs(mod);

        s += "\u00A76=\u00A7r" + (roll + statValue + mod);

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
