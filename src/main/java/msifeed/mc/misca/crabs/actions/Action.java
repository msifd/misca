package msifeed.mc.misca.crabs.actions;

import msifeed.mc.misca.crabs.rules.Effect;
import msifeed.mc.misca.crabs.rules.Modifier;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Action {
    public String name;
    public Type type;
    public ArrayList<Modifier> modifiers = new ArrayList<>();
    public ArrayList<Effect> target_effects = new ArrayList<>();
    public ArrayList<Effect> self_effects = new ArrayList<>();
    public ArrayList<String> tags = new ArrayList<>();

    public Action(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public boolean hasNoTarget() {
        switch (this.type) {
            case USE:
            case MOVE:
            case NONE:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!Action.class.isAssignableFrom(obj.getClass())) return false;
        Action act = (Action) obj;
        return this.name.equals(act.name)
                && this.type.equals(act.type)
                && this.modifiers.equals(act.modifiers)
                && this.target_effects.equals(act.target_effects)
                && this.self_effects.equals(act.self_effects)
                && this.tags.equals(act.tags);
    }

    @Override
    public String toString() {
        String rolls = this.modifiers.stream().map(Object::toString).collect(Collectors.joining(","));
        String teffs = this.target_effects.stream().map(Object::toString).collect(Collectors.joining(","));
        String seffs = this.self_effects.stream().map(Object::toString).collect(Collectors.joining(","));
        String tags = this.tags.stream().collect(Collectors.joining(","));

        return Stream.of(name, type.toString(), rolls, teffs, seffs, tags)
                .collect(Collectors.joining(":"));
    }

    public enum Type {
        MELEE, RANGED, DEFENCE, SUPPORT, ADDITIONAL, MAGIC, USE, MOVE, NONE;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}
