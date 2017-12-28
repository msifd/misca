package msifeed.mc.misca.crabs.action;

import msifeed.mc.misca.crabs.rules.Effect;
import msifeed.mc.misca.crabs.rules.Modifier;
import msifeed.mc.misca.utils.MiscaUtils;

import java.util.ArrayList;

public class Action {
    public static final Action ACTION_NONE = new Action("none", ".none", Type.PASSIVE);

    public final String name;
    public final String title;
    public final Type type;
    protected boolean deal_no_damage = false;
    public ArrayList<Modifier> modifiers = new ArrayList<>();
    public ArrayList<Effect> target_effects = new ArrayList<>();
    public ArrayList<Effect> self_effects = new ArrayList<>();
    public ArrayList<String> tags = new ArrayList<>(); // По большей части сейчас не используется

    public Action(String signature) {
        final String[] parts = signature.split("/");
        this.name = parts[0];
        this.title = parts[1];
        this.type = Action.Type.valueOf(parts[2].toUpperCase());
        this.deal_no_damage = this.type.dealNoDamage();
    }

    Action(String name, String title, Type type) {
        this.name = name;
        this.title = title;
        this.type = type;
        this.deal_no_damage = type.dealNoDamage();
    }

    /**
     * Действия без урона приимаются сразу после отписи. Кроме того ими нельзя атаковать.
     */
    public boolean dealNoDamage() {
        return deal_no_damage;
    }

    /**
     * Используется для отправки клиентам. Они не храният ничего, кроме таких сигнатур.
     * Заголовок передаем потому что есть кастомные экшны, не прописанные в .lang файлах
     */
    public String signature() {
        return name + "/" + title + "/" + type.toString();
    }

    /**
     * Красивое представление для, например, вывода в чат
     */
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

    public enum Type {
        MELEE, RANGED, MAGIC, SUPPORT, DEFENCE, PASSIVE;

        public boolean dealNoDamage() {
            return this == DEFENCE || this == PASSIVE;
        }

        public String pretty() {
            return MiscaUtils.l10n("misca.crabs.action_type." + toString());
        }

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}
