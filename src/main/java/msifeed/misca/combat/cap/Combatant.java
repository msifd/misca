package msifeed.misca.combat.cap;

import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;

public class Combatant implements ICombatant {
    private long battleId = 0;
    private float actionPoints = 0;
    private float actionPointsOverhead = 0;
    private Vec3d position = Vec3d.ZERO;
    private float trainingHealth = 0;

    @Override
    public long getBattleId() {
        return battleId;
    }

    @Override
    public void setBattleId(long value) {
        this.battleId = value;
    }

    @Override
    public float getActionPoints() {
        return actionPoints;
    }

    @Override
    public void setActionPoints(float value) {
        this.actionPoints = value;
    }

    @Override
    public float getActionPointsOverhead() {
        return actionPointsOverhead;
    }

    @Override
    public void setActionPointsOverhead(float value) {
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
}
