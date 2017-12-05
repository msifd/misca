package msifeed.mc.misca.crabs.actions;

import msifeed.mc.misca.crabs.battle.FighterContext;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum ActionManager {
    INSTANCE;

    private Logger logger = LogManager.getLogger("Crabs.Actions");

    public void doAction(FighterContext actor, FighterContext target) {
        DamageSource damage = new EntityDamageSource("crabs", actor.entity);
        target.entity.attackEntityFrom(damage, 2);
    }

    public void passDamage(FighterContext actor, EntityDamageSource damageSource) {

    }
}
