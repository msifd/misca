package msifeed.mc.misca.crabs.actions;

import msifeed.mc.misca.crabs.actions.Action.Type;
import msifeed.mc.misca.crabs.character.Stats;
import msifeed.mc.misca.crabs.rules.Effect;
import msifeed.mc.misca.crabs.rules.Effect.Damage;
import msifeed.mc.misca.crabs.rules.Effect.Fire;
import msifeed.mc.misca.crabs.rules.Modifier;
import msifeed.mc.misca.crabs.rules.Modifier.DiceG30Plus;
import msifeed.mc.misca.crabs.rules.Modifier.Stat;

import java.util.Collections;
import java.util.HashMap;

public class Actions {
    private static final HashMap<String, Action> actions = new HashMap<>();

    public static final Action test_punch = new ActionBuilder("test_punch", Type.MELEE)
            .rolls(new DiceG30Plus(), new Stat(Stats.STR))
            .target_effects(new Damage())
            .get();

    public static final Action test_fireball = new ActionBuilder("test_fireball", Type.MAGIC)
            .rolls(new DiceG30Plus(), new Stat(Stats.INT))
            .target_effects(new Fire())
            .get();

    public static Action lookup(String name) {
        return actions.get(name);
    }

    private static class ActionBuilder {
        private Action act;

        ActionBuilder(String name, Action.Type type) {
            act = new Action(name, type);
        }

        ActionBuilder rolls(Modifier... modifiers) {
            Collections.addAll(act.modifiers, modifiers);
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
