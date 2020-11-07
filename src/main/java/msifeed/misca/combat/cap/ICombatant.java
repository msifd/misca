package msifeed.misca.combat.cap;

import javax.annotation.Nonnull;
import java.util.UUID;

public interface ICombatant {
    UUID NULL_ID = new UUID(0, 0);

    default boolean isInBattle() {
        return !getBattleId().equals(NULL_ID);
    }

    @Nonnull
    UUID getBattleId();

    void setBattleId(@Nonnull UUID battleId);

    float getTrainingHealth();

    void setTrainingHealth(float health);

    default void replaceWith(@Nonnull ICombatant combatant) {
        setBattleId(combatant.getBattleId());
        setTrainingHealth(combatant.getTrainingHealth());
    }
}