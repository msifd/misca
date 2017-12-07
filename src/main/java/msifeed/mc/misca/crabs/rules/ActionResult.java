package msifeed.mc.misca.crabs.rules;

import msifeed.mc.misca.crabs.actions.Action;
import msifeed.mc.misca.crabs.battle.FighterContext;
import msifeed.mc.misca.crabs.calc.DiceMath.DiceRank;
import msifeed.mc.misca.crabs.character.Character;

import java.util.HashMap;

public final class ActionResult {
    public final FighterContext ctx;
    public final Action action;

    public HashMap<Modifier, Integer> modResults = new HashMap<>();
    public int diceSum, statSum, modSum, totalSum;
    public DiceRank diceRank = DiceRank.REGULAR;

    public ActionResult(FighterContext ctx) {
        this.ctx = ctx;
        this.action = ctx.action;
    }

    public void throwDices(Character c) {
        for (Modifier m : ctx.action.modifiers) {
            final int result = m.mod(c);
            if (m.isDice()) {
                diceSum += result;
                DiceRank rank = DiceRank.of(result);
                if (rank.compareTo(diceRank) > 0) diceRank = rank;
            } else if (m instanceof Modifier.Stat) {
                statSum += result;
            } else {
                modSum += result;
            }
            totalSum += result;
        }
    }

    public String toChatString() {
        final char diceColor = diceRank == DiceRank.LUCK
                ? '2'
                : diceRank == DiceRank.FAIL
                ? '4'
                : 'r';
        if (modSum != 0)
            return String.format("[\u00A7%c%d\u00A7r]+%d+%d=%d", diceColor, diceSum, statSum, modSum, totalSum);
        else
            return String.format("[\u00A7%c%d\u00A7r]+%d=%d", diceColor, diceSum, statSum, totalSum);
    }
}
