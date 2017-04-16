package ru.ariadna.misca.combat.fight;

public enum Action {
    INIT(0),
    MAGIC(1),
    HIT(2), SHOOT(2), SLAM(2), OTHER(2), SPECIAL(2), SAFE(2), FLEE(2),
    DEFENCE(3), DODGE(3), DUM(3), STOP(3);

    final Stage stage;

    Action(int s) {
        this.stage = Stage.values()[s];
    }

    public boolean isSystem() {
        return stage == Stage.SYSTEM;
    }

    public boolean isAttack() {
        return stage == Stage.ATTACK || stage == Stage.ANY;
    }

    public boolean isDefence() {
        return stage == Stage.DEFENCE || stage == Stage.ANY;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public enum Stage {
        SYSTEM, ANY, ATTACK, DEFENCE
    }
}
