package msifeed.misca.combat.cap;

import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;

public interface ICombatant {
    default boolean isInBattle() {
        return getBattleId() != 0;
    }
    long getBattleId();
    void setBattleId(long value);

    float getActionPoints();
    void setActionPoints(float value);

    float getActionPointsOverhead();
    void setActionPointsOverhead(float value);

    Vec3d getPosition();
    void setPosition(@Nonnull Vec3d value);

    float getTrainingHealth();
    void setTrainingHealth(float value);

    default void replaceWith(@Nonnull ICombatant com) {
        setBattleId(com.getBattleId());
        setActionPoints(com.getActionPoints());
        setActionPointsOverhead(com.getActionPointsOverhead());
        setPosition(com.getPosition());
        setTrainingHealth(com.getTrainingHealth());
    }

    default void reset() {
        setBattleId(0);
        setActionPoints(0);
        setActionPointsOverhead(0);
        setPosition(Vec3d.ZERO);
        setTrainingHealth(0);
    }
}