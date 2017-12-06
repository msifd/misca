package msifeed.mc.misca.crabs.battle;

import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSource;

public class CrabsDamage extends EntityDamageSource {
    public CrabsDamage(Entity source) {
        super("crabs", source);
    }
}
