package msifeed.misca.combat.cap;

import javax.annotation.Nonnull;
import java.util.UUID;

public class Combatant implements ICombatant {
    private UUID battleId = NULL_ID;
    private float trainingHealth = 0;

    @Override
    @Nonnull
    public UUID getBattleId() {
        return battleId;
    }

    @Override
    public void setBattleId(@Nonnull UUID battleId) {
        this.battleId = battleId;
    }

    @Override
    public float getTrainingHealth() {
        return trainingHealth;
    }

    @Override
    public void setTrainingHealth(float health) {
        trainingHealth = health;
    }
}
