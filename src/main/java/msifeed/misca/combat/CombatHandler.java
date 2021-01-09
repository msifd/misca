package msifeed.misca.combat;

import msifeed.misca.combat.battle.Battle;
import msifeed.misca.combat.battle.BattleFlow;
import msifeed.misca.combat.battle.BattleStateSync;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.CombatantSync;
import msifeed.misca.combat.cap.ICombatant;
import msifeed.misca.combat.rules.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Optional;

public class CombatHandler {
    private static final String IGNORE_PREFIX = "ignore-";
    private static final String CRITICAL_EVASION_DT = IGNORE_PREFIX + "evasion";
    public static final String NEUTRAL_PAYOUT_DT = IGNORE_PREFIX + "neutral";

    /**
     * Shitty hack to disable knock back after evasion
     */
    private boolean attackEvaded = false;

    @SubscribeEvent
    public void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        final EntityLivingBase entity = event.getEntityLiving();
        if (entity.world.isRemote || entity instanceof EntityPlayer) return;

        final ICombatant com = CombatantProvider.get(entity);
        if (!com.isInBattle()) return;
        final Battle battle = Combat.MANAGER.getBattle(com.getBattleId());
        if (battle == null || !battle.isLeader(entity.getUniqueID())) return;

        final double movementAp = Combat.getRules().movementActionPoints(com.getPosition(), entity.getPositionVector());
        if (com.getActionPoints() < movementAp) {
            BattleFlow.consumeMovementAp(entity);
            CombatantSync.sync(entity);
            Combat.MANAGER.nextTurn(battle);
        }
    }

    @SubscribeEvent
    public void onAttack(LivingAttackEvent event) {
        if (!(event.getSource() instanceof EntityDamageSource)) return;
        if (event.getSource().getDamageType().startsWith(IGNORE_PREFIX)) return;

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

        final double attackAp = Combat.getRules().attackActionPoints(srcEntity);
        if (!BattleFlow.isEnoughAp(srcEntity, com, attackAp)) {
            event.setCanceled(true);
            Combat.MANAGER.nextTurn(battle);
        }
    }

    @SubscribeEvent
    public void onHurt(LivingHurtEvent event) {
        if (event.getEntityLiving().world.isRemote) return;

        if (!(event.getSource() instanceof EntityDamageSource)) {
            handleNeutralDamage(event);
            return;
        }

        final EntityDamageSource src = (EntityDamageSource) event.getSource();
        if (!(src.getTrueSource() instanceof EntityLivingBase)) return;

        final EntityLivingBase srcEntity = (EntityLivingBase) src.getTrueSource();
        final ICombatant srcCom = CombatantProvider.get(srcEntity);
        if (!srcCom.isInBattle()) return;

        final Battle battle = Combat.MANAGER.getBattle(srcCom.getBattleId());
        if (battle == null) return;

        if (!src.getDamageType().startsWith(IGNORE_PREFIX)) {
            alterDamage(event, srcEntity);

            final boolean skipAttackAp = src.getImmediateSource() instanceof EntityPotion;
            if (!skipAttackAp)
                BattleFlow.consumeActionAp(srcEntity);
            BattleFlow.consumeMovementAp(srcEntity);
            CombatantSync.sync(srcEntity);

            if (BattleFlow.isApDepleted(srcEntity)) {
                Combat.MANAGER.nextTurn(battle);
            }
        }

        handleDeadlyAttack(event, battle);
    }

    private void handleNeutralDamage(LivingHurtEvent event) {
        final DamageSource src = event.getSource();
        if (src.canHarmInCreative()) return;

        final EntityLivingBase entity = event.getEntityLiving();
        final ICombatant com = CombatantProvider.get(entity);
        if (!com.isInBattle()) return;

        final Battle battle = Combat.MANAGER.getBattle(com.getBattleId());
        if (battle == null) return;

        final boolean isLeader = battle.isLeader(entity.getUniqueID()); // Leader takes damage immediately
        if (!isLeader && !src.getDamageType().startsWith(IGNORE_PREFIX)) {
            com.setNeutralDamage(com.getNeutralDamage() + event.getAmount());
            CombatantSync.sync(entity);
            event.setCanceled(true);
        } else {
            handleDeadlyAttack(event, battle);
        }
    }

    @SubscribeEvent
    public void onItemUseStart(LivingEntityUseItemEvent.Start event) {
        final Optional<WeaponInfo> infoOpt = Combat.getWeaponInfo(event.getItem().getItem());
        final boolean ignoreUsage = infoOpt.map(i -> i.traits.contains(WeaponTrait.ignoreUsage)).orElse(false);
        if (ignoreUsage) return;

        final EntityLivingBase entity = event.getEntityLiving();
        final ICombatant com = CombatantProvider.get(entity);
        if (!com.isInBattle()) return;

        final Battle battle = Combat.MANAGER.getBattle(com.getBattleId());
        if (battle == null) return;

        final boolean isLeader = battle.isLeader(entity.getUniqueID());
        final double usageAp = Combat.getRules().usageActionPoints(event.getItem().getItem());
        final boolean isEnoughAp = BattleFlow.isEnoughAp(entity, com, usageAp);
        if (!isLeader || !isEnoughAp) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntityLiving().world.isRemote) return;

        final Optional<WeaponInfo> infoOpt = Combat.getWeaponInfo(event.getItem().getItem());
        final boolean ignoreUsage = infoOpt.map(i -> i.traits.contains(WeaponTrait.ignoreUsage)).orElse(false);
        if (ignoreUsage) return;

        final EntityLivingBase entity = event.getEntityLiving();
        final ICombatant com = CombatantProvider.get(entity);
        if (!com.isInBattle()) return;

        final Battle battle = Combat.MANAGER.getBattle(com.getBattleId());
        if (battle == null) return;

        BattleFlow.consumeUsageAp(entity, event.getItem().getItem());
        BattleFlow.consumeMovementAp(entity);
        CombatantSync.sync(entity);
    }

    @SubscribeEvent
    public void onSplashPotion(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote || !(event.getEntity() instanceof EntityPotion)) return;

        final EntityPotion potion = (EntityPotion) event.getEntity();
        final EntityLivingBase entity = potion.getThrower();
        if (entity == null) return;

        final ICombatant com = CombatantProvider.get(entity);
        if (!com.isInBattle()) return;

        final Battle battle = Combat.MANAGER.getBattle(com.getBattleId());
        if (battle == null) return;

        if (!battle.isLeader(entity.getUniqueID())) {
            event.setCanceled(true);
            return;
        }

        if (BattleFlow.isEnoughAp(entity, com, Combat.getRules().usageActionPoints(Items.SPLASH_POTION))) {
            BattleFlow.consumeUsageAp(entity, Items.SPLASH_POTION);
            BattleFlow.consumeMovementAp(entity);
            CombatantSync.sync(entity);
        } else {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onKnockBack(LivingKnockBackEvent event) {
        if (attackEvaded) {
            attackEvaded = false;
            event.setCanceled(true);
        }
    }

    private void alterDamage(LivingHurtEvent event, EntityLivingBase srcEntity) {
        final float overrideDamage = Combat.getWeaponInfo(srcEntity, EnumHand.MAIN_HAND)
                .map(wo -> wo.damage).orElse(0f);
        final float damageAmount = event.getAmount() + overrideDamage;

        final EntityLivingBase vicEntity = event.getEntityLiving();
        final CombatantInfo srcInfo = new CombatantInfo(srcEntity, event.getSource());
        final CombatantInfo vicInfo = new CombatantInfo(vicEntity);
        final Rules rules = Combat.getRules();

        final double hitChanceRaw = rules.hitRate(srcEntity, srcInfo) - rules.evasion(vicEntity, vicInfo, srcInfo);
        final double hitChance = Math.min(hitChanceRaw, rules.maxHitChance);
        final int hitCheck = Dices.checkInt(hitChance);
        final int criticality = Dices.checkInt(rules.criticalHit(srcInfo.cs) + rules.rawChanceToHitCriticality(hitChanceRaw))
                - Dices.checkInt(rules.criticalEvasion(vicInfo.cs) + rules.rawChanceToEvadeCriticality(hitChanceRaw));
        final int successfulness = hitCheck + criticality;

        final boolean successfulHit = successfulness >= 1;
        if (successfulHit) {
            float damageFactor = 1 + rules.damageIncrease(srcInfo) - rules.damageAbsorption(vicInfo.cs);

            final boolean criticalHit = successfulness > 1;
            if (criticalHit) {
                damageFactor += 1;
                notifyActionBar("crit hit", event.getEntityLiving(), srcEntity);
            }

            event.setAmount(damageAmount * damageFactor);
        } else {
            attackEvaded = true;
            event.setCanceled(true);

            final boolean criticalEvasion = successfulness < 0;
            if (criticalEvasion) {
                // Note `src` in damageAbsorption
                final float damageFactor = 1 + rules.damageIncrease(srcInfo) - rules.damageAbsorption(srcInfo.cs);
                final DamageSource ds = new EntityDamageSource(CRITICAL_EVASION_DT, vicEntity);
                srcEntity.attackEntityFrom(ds, damageAmount * damageFactor);
                notifyActionBar("crit evasion", event.getEntityLiving(), srcEntity);
            }
        }
    }

    private static void handleDeadlyAttack(LivingHurtEvent event, Battle battle) {
        final EntityLivingBase victim = event.getEntityLiving();
        final double armorToughness = victim.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue();
        final float damage = CombatRules.getDamageAfterAbsorb(event.getAmount(), victim.getTotalArmorValue(), (float) armorToughness);

        final boolean mortalWound = victim.getHealth() - damage <= 0;
        if (!mortalWound) return;

        event.setCanceled(true);

        if (battle.isTraining()) {
            victim.setHealth(CombatantProvider.get(victim).getTrainingHealth());

            if (victim instanceof EntityPlayer) {
                ((EntityPlayer) victim).sendStatusMessage(new TextComponentString("u dead"), false);
                ((EntityPlayer) victim).inventory.damageArmor(damage);
            }
        } else {
            victim.setHealth(0.5f);

            if (battle.isLeader(victim.getUniqueID()))
                Combat.MANAGER.nextTurn(battle);
            battle.removeFromQueue(victim.getUniqueID());
            BattleStateSync.syncQueue(battle);
        }
    }

    private static void notifyActionBar(String msg, EntityLivingBase... entities) {
        for (EntityLivingBase e : entities) {
            if (e instanceof EntityPlayer)
                ((EntityPlayer) e).sendStatusMessage(new TextComponentString(msg), true);
        }
    }
}
