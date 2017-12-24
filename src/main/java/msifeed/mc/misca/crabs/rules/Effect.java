package msifeed.mc.misca.crabs.rules;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import msifeed.mc.misca.crabs.fight.CrabsDamage;
import msifeed.mc.misca.crabs.context.Context;
import net.minecraft.entity.EntityLivingBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Effect {
    private static Logger logger = LogManager.getLogger("Crabs.Effects");

    public abstract String name();

    public abstract void apply(Stage stage, ActionResult affected, ActionResult other);

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
        public void apply(Stage stage, ActionResult self, ActionResult target) {
            final Context selfCtx = self.ctx;
            final Context targetCtx = target.ctx;
            final EntityLivingBase selfEntity = self.ctx.entity;
            final EntityLivingBase targetEntity = target.ctx.entity;

            final float currentHealth = targetEntity.getHealth();
            final boolean isFatal = currentHealth <= selfCtx.damageDealt;
            final float damageToDeal = isFatal && !targetCtx.knockedOut
                    ? currentHealth - 1
                    : selfCtx.damageDealt;

            targetEntity.attackEntityFrom(new CrabsDamage(selfEntity), damageToDeal);
            if (isFatal) targetCtx.knockedOut = true;
            logger.info("Damaged {} with {} from {}", targetEntity.getCommandSenderName(), damageToDeal, selfEntity.getCommandSenderName());
        }

        @Override
        public String name() {
            return "damage";
        }
    }

    public static class Fire extends Damage {
        @Override
        public void apply(Stage stage, ActionResult self, ActionResult target) {
            super.apply(stage, self, target);
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
