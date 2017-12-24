package msifeed.mc.misca.crabs.rules;

import msifeed.mc.misca.crabs.action.Action;
import msifeed.mc.misca.crabs.context.Context;
import msifeed.mc.misca.crabs.character.Character;
import msifeed.mc.misca.crabs.character.CharacterManager;
import msifeed.mc.misca.crabs.rules.DiceMath.DiceRank;

public final class ActionResult {
    public final Context ctx;
    public final Character character;
    public final Action action;

    public int diceSum, statSum, modSum, playerMod, totalSum;
    public DiceRank diceRank = DiceRank.REGULAR;

    public ActionResult(Context ctx) {
        this.ctx = ctx;
        this.character = CharacterManager.INSTANCE.get(ctx.uuid);
        this.action = ctx.action;
    }

    public void throwDices(Character c) {
        reset(); // Also adds playerMod
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

    public int compareTo(ActionResult other) {
        if (diceRank.beats(other.diceRank)) return 1;
        else if (other.diceRank.beats(diceRank)) return -1;
        else if (other.action.type == Action.Type.PASSIVE) return 1;
        else if (this.action.type == Action.Type.PASSIVE) return -1;
        else return totalSum - other.totalSum;
    }

    private void reset() {
        diceSum = 0;
        statSum = 0;
        modSum = 0;
        playerMod = 0;
        totalSum = 0;
        playerMod = ctx.modifier;
        totalSum = playerMod;
    }
}
