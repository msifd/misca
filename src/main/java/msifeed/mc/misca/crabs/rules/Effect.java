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
            target.entity.attackEntityFrom(new CrabsDamage(self.entity), self.damageDealt);
            logger.info("Damage {} with {}", target.entity.getCommandSenderName(), self.damageDealt);
        }

        @Override
        public String toString() {
            return "damage";
        }
    }

    public static class Fire extends Effect {
        @Override
        public void apply(FighterContext self, FighterContext target) {
            target.entity.setFire(2);
            target.entity.attackEntityFrom(new CrabsDamage(self.entity), self.damageDealt);
            logger.info("Damage {} with {}", target.entity.getCommandSenderName(), self.damageDealt);
        }

        @Override
        public String toString() {
            return "fire";
        }
    }
}
