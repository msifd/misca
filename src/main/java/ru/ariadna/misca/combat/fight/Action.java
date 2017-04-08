package ru.ariadna.misca.combat.fight;

public enum Action {
    INIT(0), PASS(1),
    HIT(2), SHOOT(2), MAGIC(2), SLAM(2), OTHER(2), SPECIAL(2), SAFE(2), FLEE(2),
    DEFENCE(3), DODGE(3), DUM(3), STOP(3);

    public final Stage stage;

    Action(int s) {
        this.stage = Stage.values()[s];
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public enum Stage {
        NONE, FIGHT, ATTACK, DEFENCE
    }
}
