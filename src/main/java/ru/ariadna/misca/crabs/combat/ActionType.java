package ru.ariadna.misca.crabs.combat;

public enum ActionType {
    POINT_STRIKE, FLURRY_STRIKE, HEAVY_STRIKE,
    QUICK_SHOT, AIMED_SHOT, COMMON_SHOT,
    EVASION, BLOCK, PARRY,
    HARD_BLOW, RUDE_SHOCK, FEINT,
    POTION_THROW, ITEM, RELOAD,
    MOVE, ESCAPE, SKIP;

    public boolean isStrike() {
        return ordinal() < 3;
    }

    public boolean isShoot() {
        return ordinal() >= 3 && ordinal() < 6;
    }

    public boolean isDefence() {
        return ordinal() >= 6 && ordinal() < 9;
    }

    public boolean isSupport() {
        return ordinal() >= 9 && ordinal() < 12;
    }

    public boolean isExtra() {
        return ordinal() >= 9 && ordinal() < 12;
    }
}
