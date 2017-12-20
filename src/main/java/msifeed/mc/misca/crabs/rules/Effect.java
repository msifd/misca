package msifeed.mc.misca.crabs.rules;

import msifeed.mc.misca.crabs.battle.CrabsDamage;
import msifeed.mc.misca.crabs.battle.FighterContext;
import net.minecraft.entity.EntityLivingBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Effect {
    private static Logger logger = LogManager.getLogger("Crabs.Effects");

    public abstract String name();

    public abstract void apply(ActionResult affected, ActionResult other);

    public Stage getStage() {
        return Stage.RESULT;
    }

    @Override
    public String toString() {
        return name();
    }

    @Override
    public boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass());
    }

    // // // // // // // //

    public static class Damage extends Effect {
        @Override
        public void apply(ActionResult self, ActionResult target) {
            final FighterContext selfCtx = self.ctx;
            final FighterContext targetCtx = target.ctx;
            final EntityLivingBase selfEntity = self.ctx.entity;
            final EntityLivingBase targetEntity = target.ctx.entity;

            final float currentHealth = targetEntity.getHealth();
            final boolean isFatal = currentHealth <= selfCtx.damageDealt;
            final float damageToDeal = isFatal && targetCtx.status != FighterContext.Status.KO_ED
                    ? currentHealth - 1
                    : selfCtx.damageDealt;

            targetEntity.attackEntityFrom(new CrabsDamage(selfEntity), damageToDeal);
            if (isFatal) targetCtx.updateStatus(FighterContext.Status.KO_ED);
            logger.info("Damaged {} with {} from {}", targetEntity.getCommandSenderName(), damageToDeal, selfEntity.getCommandSenderName());
        }

        @Override
        public String name() {
            return "damage";
        }
    }

    public static class Fire extends Damage {
        @Override
        public void apply(ActionResult self, ActionResult target) {
            super.apply(self, target);
            final EntityLivingBase targetEntity = target.ctx.entity;
            targetEntity.setFire(2);
            logger.info("Fired {} with {}", targetEntity.getCommandSenderName(), self.ctx.damageDealt);
        }

        @Override
        public String name() {
            return "fire";
        }
    }

    // // // // // // // //

    public enum Stage {
        SCORE, RESULT
    }
}
