package ru.ariadna.misca.combat.fight;

public enum Action {
    INIT(0),
    PASS(1),
    HIT(2), SHOOT(2), MAGICATK(2), SLAM(2), OTHER(2), SPECIAL(2), SAFE(2), FLEE(2),
    DEFENCE(3), DODGE(3), MAGICDEF(3), DUM(3), STOP(3);

    final Stage stage;

    Action(int s) {
        this.stage = Stage.values()[s];
    }

    public boolean isFight() {
        return stage == Stage.ATTACK || stage == Stage.FIGHT;
    }

    public boolean isDefence() {
        return stage == Stage.DEFENCE || stage == Stage.FIGHT;
    }

    public boolean isSystem() {
        return stage == Stage.SYSTEM;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public enum Stage {
        SYSTEM, FIGHT, ATTACK, DEFENCE
    }
}
