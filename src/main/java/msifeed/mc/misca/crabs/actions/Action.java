package msifeed.mc.misca.crabs.actions;

import msifeed.mc.misca.crabs.rules.Effect;
import msifeed.mc.misca.crabs.rules.Roll;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Action {
    public String name;
    public Type type;
    public LinkedList<Roll> rolls = new LinkedList<>();
    public LinkedList<Effect> target_effects = new LinkedList<>();
    public LinkedList<Effect> self_effects = new LinkedList<>();
    public LinkedList<String> tags = new LinkedList<>();

    public Action(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (!Action.class.isAssignableFrom(obj.getClass())) return false;
        Action act = (Action) obj;
        return this.name.equals(act.name)
                && this.type.equals(act.type)
                && this.rolls.equals(act.rolls)
                && this.target_effects.equals(act.target_effects)
                && this.self_effects.equals(act.self_effects)
                && this.tags.equals(act.tags);
    }

    @Override
    public String toString() {
        String rolls = this.rolls.stream().map(Object::toString).collect(Collectors.joining(","));
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
