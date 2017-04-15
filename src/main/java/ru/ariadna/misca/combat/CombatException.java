package ru.ariadna.misca.combat;

import cpw.mods.fml.common.registry.LanguageRegistry;

public class CombatException extends Exception {
    public final Type type;

    public CombatException(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        String key;
        switch (type) {
            case NO_FIGHT:
                key = "misca.combat.cmb.error.no_fight";
                break;
            case WRONG_STAGE:
                key = "misca.combat.cmb.error.wrong_stage";
                break;
            case NO_TARGET:
                key = "misca.combat.cmb.error.no_target";
                break;
            default:
                return "Unknown combat exception type, baka!";
        }
        return LanguageRegistry.instance().getStringLocalization(key);
    }

    public enum Type {
        NO_FIGHT, WRONG_STAGE, NO_TARGET;
    }
}
