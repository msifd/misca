package msifeed.misca.combat;

import msifeed.misca.charsheet.cap.Charsheet;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.charsheet.cap.ICharsheet;
import msifeed.misca.combat.battle.Battle;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.ICombatant;
import msifeed.misca.combat.rules.Dices;
import msifeed.misca.combat.rules.Rules;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CombatHandler {
    private static final String CRITICAL_EVASION_DAMAGE_TYPE = "criticalEvasion";

    private final Charsheet defaultCharsheet = new Charsheet();

    CombatHandler() {
        this.defaultCharsheet.attrs().setAll(5);
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if (event.getSource().getDamageType().equals(CRITICAL_EVASION_DAMAGE_TYPE)) return;
        if (!(event.getSource() instanceof EntityDamageSource)) return;

        final EntityDamageSource src = (EntityDamageSource) event.getSource();
        if (!(src.getTrueSource() instanceof EntityLivingBase)) return;

        final EntityLivingBase srcEntity = (EntityLivingBase) src.getTrueSource();
        final ICombatant com = CombatantProvider.get(srcEntity);
        if (!com.isInBattle()) return;

        final Battle battle = Combat.MANAGER.getBattle(com.getBattleId());
        if (battle == null) return;
        if (!battle.isLeader(srcEntity.getUniqueID())) {
            event.setCanceled(true);
            notifyActionBar("not your turn", srcEntity);
            return;
        }

        final double movementAp = Rules.movementActionPoints(com.getPosition(), srcEntity.getPositionVector());
        final double attackAp = Rules.attackActionPoints(srcEntity);
        final double totalAp = movementAp + attackAp;

        if (com.getActionPoints() < totalAp) {
            event.setCanceled(true);
            final boolean movementCheck = !(srcEntity instanceof EntityPlayer) || movementAp < 1;
            final double targetAp = srcEntity instanceof EntityPlayer ? attackAp : totalAp;
            if (movementCheck && com.getActionPoints() < targetAp) {
                battle.finishTurn();
            }
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getDamageType().equals(CRITICAL_EVASION_DAMAGE_TYPE)) return;
        if (!(event.getSource() instanceof EntityDamageSource)) return;

        final EntityDamageSource src = (EntityDamageSource) event.getSource();
        if (!(src.getTrueSource() instanceof EntityLivingBase)) return;

        final EntityLivingBase srcEntity = (EntityLivingBase) src.getTrueSource();
        final ICombatant srcCom = CombatantProvider.get(srcEntity);
        if (!srcCom.isInBattle()) return;

        checkChances(event, srcEntity);
        checkActionPoints(event, srcEntity, srcCom);
        updateTurn(srcEntity);
    }

    private void checkChances(LivingHurtEvent event, EntityLivingBase srcEntity) {
        final EntityLivingBase victim = event.getEntityLiving();
        final ICharsheet srcCs = CharsheetProvider.getOr(srcEntity, defaultCharsheet);
        final ICharsheet vicCs = CharsheetProvider.getOr(victim, defaultCharsheet);

        final double hitChance = Rules.hitRate(srcCs) - Rules.evasion(vicCs);
        final double criticality = Dices.checkFloat(Rules.criticalHit(srcCs)) - Dices.checkFloat(Rules.criticalEvasion(vicCs));

        if (Dices.check(hitChance + criticality)) {
            double damageMultiplier = 1 + Rules.damageIncrease(srcCs) + Rules.damageAbsorption(vicCs);

            if (criticality > 0) {
                damageMultiplier += 1;
                notifyActionBar("crit hit", event.getEntityLiving(), srcEntity);
            }

            event.setAmount((float) (event.getAmount() * damageMultiplier));

            final Battle battle = Combat.MANAGER.getEntityBattle(srcEntity);
            final boolean training = battle != null && battle.isTraining();
            final boolean mortalWound = victim.getHealth() - event.getAmount() <= 0;
            if (training && mortalWound) {
                victim.setHealth(CombatantProvider.get(victim).getTrainingHealth());
                if (victim instanceof EntityPlayer)
                    ((EntityPlayer) victim).sendStatusMessage(new TextComponentString("u dead"), false);
            }
        } else {
            event.setCanceled(true);

            if (criticality < 0) {
                final double damageMultiplier = 1 + Rules.damageIncrease(srcCs) + Rules.damageAbsorption(srcCs);
                final DamageSource ds = new EntityDamageSourceIndirect(CRITICAL_EVASION_DAMAGE_TYPE, srcEntity, event.getEntity());
                srcEntity.attackEntityFrom(ds, (float) (event.getAmount() * damageMultiplier));
                notifyActionBar("crit evasion", event.getEntityLiving(), srcEntity);
            }
        }
    }

    private void checkActionPoints(LivingHurtEvent event, EntityLivingBase entity, ICombatant com) {
        final double requiredAp = com.getActionPointsOverhead() + Rules.attackActionPoints(entity);
        if (com.getActionPoints() < requiredAp)
            event.setCanceled(true);
    }

    private void updateTurn(EntityLivingBase entity) {
        final Battle battle = Combat.MANAGER.getEntityBattle(entity);
        if (battle != null && battle.isLeader(entity.getUniqueID()))
            battle.makeAction();
    }

    private static void notifyActionBar(String msg, EntityLivingBase... entities) {
        for (EntityLivingBase e : entities) {
            if (e instanceof EntityPlayer)
                ((EntityPlayer) e).sendStatusMessage(new TextComponentString(msg), true);
        }
    }
}
