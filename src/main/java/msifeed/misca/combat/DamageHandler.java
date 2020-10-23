package msifeed.misca.combat;

import msifeed.misca.charsheet.cap.CharAttribute;
import msifeed.misca.charsheet.cap.Charsheet;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.charsheet.cap.ICharsheet;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DamageHandler {
    private static final String CRITICAL_EVASION_TYPE = "criticalEvasion";

    private final Charsheet defaultCharsheet = new Charsheet();

    DamageHandler() {
        for (CharAttribute attr : CharAttribute.values())
            defaultCharsheet.setAttribute(attr, 5);
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getSource() instanceof EntityDamageSource)) return;

        final EntityDamageSource src = (EntityDamageSource) event.getSource();
        if (src.getDamageType().equals(CRITICAL_EVASION_TYPE)) return;
        if (!(src.getTrueSource() instanceof EntityLivingBase)) return;

        final EntityLivingBase srcEntity = (EntityLivingBase) src.getTrueSource();
        final ICharsheet srcCs = CharsheetProvider.getOr(srcEntity, defaultCharsheet);
        final ICharsheet vicCs = CharsheetProvider.getOr(event.getEntityLiving(), defaultCharsheet);

        final float hitChance = Rules.hitRate(srcCs) - Rules.evasion(vicCs);
        final float criticality = Dices.checkFloat(Rules.criticalHit(srcCs)) - Dices.checkFloat(Rules.criticalEvasion(vicCs));

        if (Dices.check(hitChance + criticality)) {
            float damageMultiplier = 1 + Rules.damageIncrease(srcCs) + Rules.damageAbsorption(vicCs);

            if (criticality > 0) {
                damageMultiplier += 1;
                notifyActionBar(event.getEntityLiving(), srcEntity, "crit hit");
            }

            event.setAmount(event.getAmount() * damageMultiplier);
        } else {
            event.setCanceled(true);

            if (criticality < 0) {
                final float damageMultiplier = 1 + Rules.damageIncrease(srcCs) + Rules.damageAbsorption(srcCs);
                final DamageSource ds = new EntityDamageSourceIndirect(CRITICAL_EVASION_TYPE, srcEntity, event.getEntity());
                srcEntity.attackEntityFrom(ds, event.getAmount() * damageMultiplier);
                notifyActionBar(event.getEntityLiving(), srcEntity, "crit evasion");
            }
        }
    }

    private static void notifyActionBar(EntityLivingBase vic, EntityLivingBase src, String msg) {
        if (vic instanceof EntityPlayer)
            ((EntityPlayer) vic).sendStatusMessage(new TextComponentString(msg), true);
        if (src instanceof EntityPlayer)
            ((EntityPlayer) src).sendStatusMessage(new TextComponentString(msg), true);
    }
}
