package msifeed.mc.misca.crabs.action;

import msifeed.mc.misca.crabs.rules.Effect;
import msifeed.mc.misca.crabs.rules.Modifier;
import msifeed.mc.misca.utils.MiscaUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

public class Action {
    public static final Action ACTION_NONE = new Action("none", ".none", Type.PASSIVE);

    public final String name;
    public final String title;
    public final Type type;
    public boolean defencive;
    public ArrayList<Modifier> modifiers = new ArrayList<>();
    public ArrayList<Effect> target_effects = new ArrayList<>();
    public ArrayList<Effect> self_effects = new ArrayList<>();

    public Action(String signature) {
        final String[] parts = signature.split("/");
        final Iterator<String> it = Stream.of(parts).iterator();

        this.name = it.next();
        this.title = it.next();
        this.type = Action.Type.valueOf(it.next().toUpperCase());
        this.defencive = this.type.defencive();

        while (it.hasNext()) {
            switch (it.next()) {
                case "defencive":
                    this.defencive = true;
                    break;
            }
        }
    }

    Action(String name, String title, Type type) {
        this.name = name;
        this.title = title;
        this.type = type;
        this.defencive = type.defencive();
    }

    public boolean isDefencive() {
        return defencive;
    }

    /**
     * Используется для отправки клиентам. Они не храният ничего, кроме таких сигнатур.
     * Заголовок передаем потому что есть кастомные экшны, не прописанные в .lang файлах
     */
    public String signature() {
        String s = name + "/"
                + title + "/"
                + type.toString();

        if (defencive) s += "/defencive";

        return s;
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
                && this.defencive == act.defencive
                && this.modifiers.equals(act.modifiers)
                && this.target_effects.equals(act.target_effects)
                && this.self_effects.equals(act.self_effects);
    }

    public enum Type {
        MELEE, RANGED, MAGIC, SUPPORT, DEFENCE, PASSIVE;

        public boolean defencive() {
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
