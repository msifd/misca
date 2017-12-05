package msifeed.mc.misca.crabs.actions;

import msifeed.mc.misca.crabs.actions.Action.Type;
import msifeed.mc.misca.crabs.character.Stats;
import msifeed.mc.misca.crabs.rules.Effect;
import msifeed.mc.misca.crabs.rules.Effect.Damage;
import msifeed.mc.misca.crabs.rules.Roll;
import msifeed.mc.misca.crabs.rules.Roll.Const;
import msifeed.mc.misca.crabs.rules.Roll.DiceG30Plus;
import msifeed.mc.misca.crabs.rules.Roll.Stat;

import java.util.Collections;
import java.util.HashMap;

public class Actions {
    public static final HashMap<String, Action> actions = new HashMap<>();

    public static final Action point_hit = new ActionBuilder("point_hit", Type.MELEE)
            .rolls(new DiceG30Plus(),
                    new Stat(Stats.STR),
                    new Stat(Stats.PER),
                    new Stat(Stats.INT),
                    new Const(-5))
            .target_effects(new Damage())
            .get();

    private static class ActionBuilder {
        private Action act;

        ActionBuilder(String name, Action.Type type) {
            act = new Action(name, type);
        }

        ActionBuilder rolls(Roll... rolls) {
            Collections.addAll(act.rolls, rolls);
            return this;
        }

        ActionBuilder target_effects(Effect... effects) {
            Collections.addAll(act.target_effects, effects);
            return this;
        }

        Action get() {
            actions.put(act.name, act);
            return act;
        }
    }
}
