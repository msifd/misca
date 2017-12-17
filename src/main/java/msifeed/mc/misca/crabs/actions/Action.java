package msifeed.mc.misca.crabs.actions;

import msifeed.mc.misca.crabs.rules.Effect;
import msifeed.mc.misca.crabs.rules.Modifier;
import msifeed.mc.misca.utils.MiscaUtils;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Action {
    public final String name;
    public final String title;
    public final Type type;
    protected boolean deal_no_damage = false;
    public ArrayList<Modifier> modifiers = new ArrayList<>();
    public ArrayList<Effect> target_effects = new ArrayList<>();
    public ArrayList<Effect> self_effects = new ArrayList<>();
    public ArrayList<String> tags = new ArrayList<>(); // По большей части сейчас не используется

    Action(String name, String title, Type type) {
        this.name = name;
        this.title = title;
        this.type = type;
    }

    public boolean dealNoDamage() {
        if (deal_no_damage) return true;
        switch (type) {
            case OTHER:
                return true;
            default:
                return false;
        }
    }

    public String signature() {
        return name + "/" + title + "/" + type.toString();
    }

    public String pretty() {
        return title.length() > 1 && title.charAt(0) == '.'
                ? MiscaUtils.l10n("misca.crabs.action." + title.substring(1))
                : title;
    }

    @Override
    public boolean equals(Object obj) {
        if (!Action.class.isAssignableFrom(obj.getClass())) return false;
        final Action act = (Action) obj;
        return this.name.equals(act.name)
                && this.type.equals(act.type)
                && this.modifiers.equals(act.modifiers)
                && this.target_effects.equals(act.target_effects)
                && this.self_effects.equals(act.self_effects)
                && this.tags.equals(act.tags);
    }

    @Override
    public String toString() {
        final String rolls = this.modifiers.stream().map(Object::toString).collect(Collectors.joining(","));
        final String teffs = this.target_effects.stream().map(Object::toString).collect(Collectors.joining(","));
        final String seffs = this.self_effects.stream().map(Object::toString).collect(Collectors.joining(","));
        final String tags = this.tags.stream().collect(Collectors.joining(","));
        return Stream.of(name, title, type.toString(), rolls, teffs, seffs, tags)
                .collect(Collectors.joining(":"));
    }

    public enum Type {
        MELEE, RANGED, MAGIC, DEFENCE, SUPPORT, OTHER;

        @Override
        public String toString() {
            return name().toLowerCase();
        }

        public String pretty() {
            return MiscaUtils.l10n("misca.crabs.action_type." + toString());
        }
    }
}
