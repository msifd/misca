package msifeed.mc.misca.crabs.battle;

import msifeed.mc.misca.crabs.character.Character;
import msifeed.mc.misca.crabs.character.Stats;
import msifeed.mc.misca.crabs.rules.ActionResult;
import msifeed.mc.misca.crabs.rules.DiceMath;
import msifeed.mc.misca.utils.MiscaUtils;
import net.minecraft.entity.EntityLivingBase;

public final class MoveFormatter {
    public static String formatActionResults(ActionResult winner, ActionResult looser) {
        EntityLivingBase we = winner.ctx.entity, le = looser.ctx.entity;
        return "\u00A76[\u00A72"
                + we.getCommandSenderName()
                + "\u00A76>\u00A74"
                + le.getCommandSenderName()
                + "\u00A76]:\u00A7r "
                + formatActionResult(winner)
                + " \u00A76>\u00A7r "
                + formatActionResult(looser);
    }

    public static String formatActionResult(ActionResult action) {
        // Абилки начинающиеся с точки получают локализованный заголовок
        final String rawTitle = action.action.title;
        final String title = rawTitle.length() > 1 && rawTitle.charAt(0) == '.'
                ? MiscaUtils.l10n("misca.crabs.action." + rawTitle.substring(1))
                : rawTitle;
        String s = "\u00A76"
                + title
                + ": "
                + "["
                + diceRankColor(action.diceRank)
                + (action.totalSum - action.playerMod);

        if (action.playerMod != 0) {
            s += "\u00A76"
                    + (action.playerMod > 0 ? "+" : "-")
                    + "\u00A7r"
                    + Math.abs(action.playerMod);
        }

        s += "\u00A76]\u00A7r";

        return s;
    }

    public static String formatStatRoll(Character character, Stats stat, int roll, int mod) {
        final int statValue = character.stat(stat);
        String s = "\u00A76["
                + character.name
                + "] "
                + stat.toString()
                + ": ["
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
