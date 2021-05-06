package msifeed.misca.tweaks;

import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DisableSomeDamageTypes {
    @SubscribeEvent
    public void onDamaged(LivingAttackEvent event) {
        if (event.getSource() == DamageSource.IN_WALL) {
            event.setCanceled(true);
        }
    }
}
