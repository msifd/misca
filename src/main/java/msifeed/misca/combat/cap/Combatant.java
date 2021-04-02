package msifeed.misca.combat.cap;

import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;

public class Combatant implements ICombatant {
    private long battleId = 0;
    private int puppet = -1;
    private double actionPoints = 0;
    private double actionPointsOverhead = 0;
    private Vec3d position = Vec3d.ZERO;
    private float trainingHealth = 0;
    private float neutralDamage = 0;

    @Override
    public long getBattleId() {
        return battleId;
    }

    @Override
    public void setBattleId(long value) {
        this.battleId = value;
    }

    @Override
    public int getPuppet() {
        return puppet;
    }

    @Override
    public void setPuppet(int value) {
        this.puppet = value;
    }

    @Override
    public double getActionPoints() {
        return actionPoints;
    }

    @Override
    public void setActionPoints(double value) {
        this.actionPoints = value;
    }

    @Override
    public double getActionPointsOverhead() {
        return actionPointsOverhead;
    }

    @Override
    public void setActionPointsOverhead(double value) {
        this.actionPointsOverhead = value;
    }

    @Override
    public Vec3d getPosition() {
        return position;
    }

    @Override
    public void setPosition(@Nonnull Vec3d value) {
        this.position = value;
    }

    @Override
    public float getTrainingHealth() {
        return trainingHealth;
    }

    @Override
    public void setTrainingHealth(float value) {
        this.trainingHealth = value;
    }

    @Override
    public float getNeutralDamage() {
        return neutralDamage;
    }

    @Override
    public void setNeutralDamage(float value) {
        this.neutralDamage = value;
    }
}
