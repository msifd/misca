package msifeed.mc.misca.crabs.rules;

import java.util.List;

public class Action {
    public String name;
    public final Type type;
    public List<Roll> rolls;
    public List<Effect> target_effects;
    public List<Effect> self_effects;
    public List<Type> affected;

    Action(Type type) {
        this.type = type;
    }

    public enum Type {
        MELEE, RANGED, DEFENCE, SUPPORT, ADDITIONAL, MAGIC, USE, MOVE, NONE;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}
