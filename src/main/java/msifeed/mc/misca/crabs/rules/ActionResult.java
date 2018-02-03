package msifeed.mc.misca.crabs.rules;

import msifeed.mc.misca.crabs.action.Action;
import msifeed.mc.misca.crabs.character.Character;
import msifeed.mc.misca.crabs.character.CharacterManager;
import msifeed.mc.misca.crabs.context.Context;
import msifeed.mc.misca.crabs.rules.DiceMath.DiceRank;

public final class ActionResult {
    public final Context ctx;
    public final Character character;
    public final Action action;

    public boolean actionSuccessful;
    public int playerMod, effectMod;
    public int diceSum, statSum, modSum, totalSum;
    public DiceRank diceRank = DiceRank.REGULAR;

    public float damageToReceive = 0;

    public ActionResult(Context ctx) {
        this.ctx = ctx;
        this.character = CharacterManager.INSTANCE.get(ctx.uuid);
        this.action = ctx.action;
        this.playerMod = ctx.modifier;
    }

    public void throwDices(Character c) {
        reset();
        totalSum += playerMod + effectMod;

        for (final Modifier m : ctx.action.modifiers) {
            final int result = m.mod(c);
            if (m.isDice()) {
                diceSum += result;
                final DiceRank rank = DiceRank.ofD40(result);
                if (rank.compareTo(diceRank) > 0)
                    diceRank = rank; // Если несколько дайсов, то выбираем наилучший критический ранг
            } else if (m instanceof Modifier.Stat) {
                statSum += result;
            } else {
                modSum += result;
            }
            totalSum += result;
        }

        if (diceRank == DiceRank.FAIL)
            actionSuccessful = false;
    }

    public int compareTo(ActionResult other) {
        if (diceRank.beats(other.diceRank)) return 1;
        else if (other.diceRank.beats(diceRank)) return -1;

        else if (actionSuccessful && !other.actionSuccessful) return 1;
        else if (!actionSuccessful && other.actionSuccessful) return -1;

        else return totalSum - other.totalSum;
    }

    private void reset() {
        actionSuccessful = !ctx.knockedOut;
        diceSum = 0;
        statSum = 0;
        modSum = 0;
        totalSum = 0;
    }
}
