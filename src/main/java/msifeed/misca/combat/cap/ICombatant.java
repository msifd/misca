package msifeed.misca.combat.cap;

import msifeed.misca.Misca;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;

public interface ICombatant {
    ResourceLocation KEY = new ResourceLocation(Misca.MODID, "combat");

    default boolean isInBattle() {
        return getBattleId() != 0;
    }
    long getBattleId();
    void setBattleId(long value);

    default boolean hasPuppet() {
        return getPuppet() >= 0;
    }
    int getPuppet();
    void setPuppet(int value);
    default void resetPuppet() {
        setPuppet(-1);
    }

    double getActionPoints();
    void setActionPoints(double value);
    default void addActionPoints(double value) {
        setActionPoints(getActionPoints() + value);
    }

    double getActionPointsOverhead();
    void setActionPointsOverhead(double value);

    Vec3d getPosition();
    void setPosition(@Nonnull Vec3d value);

    float getTrainingHealth();
    void setTrainingHealth(float value);

    float getNeutralDamage();
    void setNeutralDamage(float value);

    default void replaceWith(@Nonnull ICombatant com) {
        setBattleId(com.getBattleId());
        setPuppet(com.getPuppet());
        setActionPoints(com.getActionPoints());
        setActionPointsOverhead(com.getActionPointsOverhead());
        setPosition(com.getPosition());
        setTrainingHealth(com.getTrainingHealth());
        setNeutralDamage(com.getNeutralDamage());
    }

    default void reset() {
        setBattleId(0);
        resetPuppet();
        setActionPoints(0);
        setActionPointsOverhead(0);
        setPosition(Vec3d.ZERO);
        setTrainingHealth(0);
        setNeutralDamage(0);
    }
}