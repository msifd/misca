package msifeed.mc.misca.crabs.rules;

import msifeed.mc.misca.crabs.battle.CrabsDamage;
import msifeed.mc.misca.crabs.battle.FighterContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Effect {
    private static Logger logger = LogManager.getLogger("Crabs.Effects");

    Effect() {
    }

    public abstract void apply(FighterContext self, FighterContext target);

    @Override
    public boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass());
    }

    public static class Damage extends Effect {
        @Override
        public void apply(FighterContext self, FighterContext target) {
            final float currentHealth = target.entity.getHealth();
            final boolean isFatal = currentHealth <= self.damageDealt;
            final float damageToDeal = isFatal && target.status != FighterContext.Status.KO_ED
                    ? currentHealth - 1
                    : self.damageDealt;

            target.entity.attackEntityFrom(new CrabsDamage(self.entity), damageToDeal);
            if (isFatal) target.updateStatus(FighterContext.Status.KO_ED);
            logger.info("Damaged {} with {} from {}", target.entity.getCommandSenderName(), damageToDeal, self.entity.getCommandSenderName());
        }

        @Override
        public String toString() {
            return "damage";
        }
    }

    public static class Fire extends Damage {
        @Override
        public void apply(FighterContext self, FighterContext target) {
            super.apply(self, target);
            target.entity.setFire(2);
            logger.info("Fired {} with {}", target.entity.getCommandSenderName(), self.damageDealt);
        }

        @Override
        public String toString() {
            return "fire";
        }
    }
}
