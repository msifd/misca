package msifeed.misca.combat;

import msifeed.misca.combat.battle.Battle;
import msifeed.misca.combat.battle.BattleFlow;
import msifeed.misca.combat.battle.BattleStateSync;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.CombatantSync;
import msifeed.misca.combat.cap.ICombatant;
import msifeed.misca.combat.rules.*;
import msifeed.misca.rolls.Dices;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CombatHandler {
    private static final String IGNORE_PREFIX = "ignore-";
    private static final String CRITICAL_EVASION_DT = IGNORE_PREFIX + "evasion";
    public static final String NEUTRAL_PAYOUT_DT = IGNORE_PREFIX + "neutral";

    /**
     * Shitty hack to disable knock back after evasion
     */
    private boolean attackEvaded = false;

    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityLivingBase) {
            final AbstractAttributeMap attributes = ((EntityLivingBase) event.getObject()).getAttributeMap();

            attributes.registerAttribute(CharAttribute.MOD);
            for (CharAttribute attr : CharAttribute.values())
                attributes.registerAttribute(attr.attribute);
        }
    }

    /**
     * Track entity movement and end its turn when AP depletes.
     */
    @SubscribeEvent
    public void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        final EntityLivingBase entity = event.getEntityLiving();
        if (entity.world.isRemote || entity instanceof EntityPlayer) return;

        final ICombatant com = CombatantProvider.get(entity);
        if (!com.isInBattle()) return;
        final Battle battle = Combat.MANAGER.getBattle(com.getBattleId());
        if (battle == null || !battle.isLeader(entity.getUniqueID())) return;

        final double movementAp = Combat.getRules().movementActionPoints(entity, com.getPosition(), entity.getPositionVector());
        if (com.getActionPoints() <= 0 || com.getActionPoints() < movementAp) {
            BattleFlow.consumeMovementAp(entity);
            CombatantSync.sync(entity);
            Combat.MANAGER.finishTurn(battle);
        }
    }

    @SubscribeEvent
    public void onPlayerAttackEmptySpace(PlayerInteractEvent.LeftClickEmpty event) {
        if (event.getEntityLiving().world.isRemote) return;

        CombatFlow.onSourceAttack(event.getEntityLiving());
    }

    @SubscribeEvent
    public void onPlayerAttackBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getEntityLiving().world.isRemote) return;

        CombatFlow.onSourceAttack(event.getEntityLiving());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerAttackEntity(AttackEntityEvent event) {
        if (event.getEntityLiving().world.isRemote || event.isCanceled()) return;

        final ResourceLocation weapon = event.getEntityPlayer().getHeldItemMainhand().getItem().getRegistryName();
        CombatFlow.trySourceAttack(event.getEntityLiving(), weapon);
    }

    @SubscribeEvent
    public void onAttack(LivingAttackEvent event) {
        if (!(event.getSource() instanceof EntityDamageSource)) return;
        if (event.getSource().getDamageType().startsWith(IGNORE_PREFIX)) return;

        final EntityDamageSource src = (EntityDamageSource) event.getSource();
        if (!(src.getTrueSource() instanceof EntityLivingBase)) return;

        final EntityLivingBase source = (EntityLivingBase) src.getTrueSource();
        final ResourceLocation weapon = source.getHeldItemMainhand().getItem().getRegistryName();
        final EntityLivingBase actor = CombatFlow.getCombatActor(source);
        if (actor == null) return;

        if (!CombatFlow.canAttack(actor, weapon)) {
            event.setCanceled(true);
        }

        final boolean isNotPlayer = !(actor instanceof EntityPlayer);
        if (isNotPlayer) {
            if (event.isCanceled()) {
                // Mobs cant finish their turn, so help them
                final ICombatant com = CombatantProvider.get(actor);
                final Battle battle = Combat.MANAGER.getBattle(com.getBattleId());
                if (battle != null)
                    Combat.MANAGER.finishTurn(battle);
            } else {
                // Players consume AP on swing, mobs on attack
                CombatFlow.onAttack(actor, weapon);
            }
        }
    }

    @SubscribeEvent
    public void onHurt(LivingHurtEvent event) {
        // Neutral damage has no rolls
        if (!(event.getSource() instanceof EntityDamageSource)) {
            handleNeutralDamage(event);
            return;
        }

        if (event.getEntityLiving().world.isRemote) return;

        final EntityDamageSource src = (EntityDamageSource) event.getSource();
        if (!(src.getTrueSource() instanceof EntityLivingBase)) return;

        final EntityLivingBase srcEntity = (EntityLivingBase) src.getTrueSource();
        final EntityLivingBase actor = CombatFlow.getCombatActor(srcEntity);
        if (actor == null) return;

        if (!src.getDamageType().startsWith(IGNORE_PREFIX)) {
            final ResourceLocation weapon = srcEntity.getHeldItemMainhand().getItem().getRegistryName();
            alterDamage(event, actor, weapon);
        }

        final ICombatant srcCom = CombatantProvider.get(srcEntity);
        final Battle battle = Combat.MANAGER.getBattle(srcCom.getBattleId());
        handleDeadlyAttack(event, event.getAmount(), event.getEntityLiving(), battle);
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
            handleDeadlyAttack(event, event.getAmount(), event.getEntityLiving(), battle);
        }
    }

    @SubscribeEvent
    public void onItemUse(PlayerInteractEvent.RightClickItem event) {
        final ResourceLocation weapon = event.getItemStack().getItem().getRegistryName();
        final WeaponInfo info = WeaponRegistry.getWeaponInfo(weapon);

        final boolean canUse = info.has(WeaponTrait.canUse);
        if (!canUse) return;
        if (event.getItemStack().getItemUseAction() == EnumAction.NONE && info.traits.isEmpty()) return;

        final EntityLivingBase actor = CombatFlow.getCombatActor(event.getEntityLiving());
        if (actor == null) return;

        if (CombatFlow.canUse(actor, weapon)) {
            if (!actor.world.isRemote)
                CombatFlow.onUse(actor, weapon);
        } else {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onItemUseStart(LivingEntityUseItemEvent.Start event) {
        final ResourceLocation weapon = event.getItem().getItem().getRegistryName();
        final boolean canUse = WeaponRegistry.getWeaponInfo(weapon).has(WeaponTrait.canHoldUse);
        if (!canUse) return;

        final EntityLivingBase actor = CombatFlow.getCombatActor(event.getEntityLiving());
        if (actor == null) return;

        if (!CombatFlow.canUse(actor, weapon)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onItemUseStop(LivingEntityUseItemEvent.Stop event) {
        if (event.getEntityLiving().world.isRemote) return;

        final ResourceLocation weapon = event.getItem().getItem().getRegistryName();
        final boolean canUse = WeaponRegistry.getWeaponInfo(weapon).has(WeaponTrait.canHoldUse);
        if (!canUse) return;

        final EntityLivingBase actor = CombatFlow.getCombatActor(event.getEntityLiving());
        if (actor == null) return;

        CombatFlow.onUse(actor, weapon);
    }

    @SubscribeEvent
    public void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntityLiving().world.isRemote) return;

        final ResourceLocation weapon = event.getItem().getItem().getRegistryName();
        final boolean canUse = WeaponRegistry.getWeaponInfo(weapon).has(WeaponTrait.canHoldUse);
        if (!canUse) return;

        final EntityLivingBase actor = CombatFlow.getCombatActor(event.getEntityLiving());
        if (actor == null) return;

        CombatFlow.onUse(actor, weapon);
    }

    @SubscribeEvent
    public void onSplashPotion(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote || !(event.getEntity() instanceof EntityPotion)) return;

        final EntityPotion potion = (EntityPotion) event.getEntity();
        final EntityLivingBase srcEntity = potion.getThrower();
        if (srcEntity == null) return;

        final EntityLivingBase actor = CombatFlow.getCombatActor(srcEntity);
        if (actor == null) return;

        if (CombatFlow.canUse(actor, Items.SPLASH_POTION.getRegistryName())) {
            CombatFlow.onUse(actor, Items.SPLASH_POTION.getRegistryName());
        } else {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onKnockBack(LivingKnockBackEvent event) {
        if (attackEvaded) {
            attackEvaded = false;
            event.setCanceled(true);
        }
    }

    private void alterDamage(LivingHurtEvent event, EntityLivingBase actor, ResourceLocation weapon) {
        final float damageOverride = WeaponRegistry.getWeaponInfo(weapon).dmg;
        final float damageAmount = damageOverride > 0 ? damageOverride : event.getAmount();

        final EntityLivingBase vicEntity = event.getEntityLiving();
        final CombatantInfo actInfo = new CombatantInfo(actor, event.getSource(), weapon);
        final CombatantInfo vicInfo = new CombatantInfo(vicEntity);
        final Rules rules = Combat.getRules();

        final double hitChanceRaw = rules.hitChance(actInfo, weapon) - rules.evasionChance(vicEntity, vicInfo, actInfo);
        final double hitChance = Math.min(hitChanceRaw, rules.maxHitChance);
        final int hitCheck = Dices.checkInt(hitChance);
        final int criticality = Dices.checkInt(rules.criticalHit(actInfo) + rules.rawChanceToHitCriticality(hitChanceRaw))
                - Dices.checkInt(rules.criticalEvasion(vicInfo) + rules.rawChanceToEvadeCriticality(hitChanceRaw));
        final int successfulness = hitCheck + criticality;

        final boolean successfulHit = successfulness >= 1;
        if (successfulHit) {
            float damageFactor = 1 + rules.damageIncrease(actInfo) - rules.damageAbsorption(vicInfo);

            final boolean criticalHit = successfulness > 1;
            if (criticalHit) {
                damageFactor += 1;
                notifyActionBar("crit hit", event.getEntityLiving(), actor);
            }

            event.setAmount(damageAmount * damageFactor);
        } else {
            attackEvaded = true;
            event.setCanceled(true);

            final boolean criticalEvasion = successfulness < 0;
            if (criticalEvasion) {
                // Note `src` in damageAbsorption
                final float damageFactor = 1 + rules.damageIncrease(actInfo) - rules.damageAbsorption(actInfo);
                final DamageSource ds = new EntityDamageSource(CRITICAL_EVASION_DT, vicEntity);
                actor.attackEntityFrom(ds, damageAmount * damageFactor);
                notifyActionBar("crit evasion", event.getEntityLiving(), actor);
            }
        }
    }

    private void handleDeadlyAttack(Event event, float amount, EntityLivingBase entity, Battle battle) {
        if (!(entity instanceof EntityPlayer)) return;

        final EntityPlayer victim = (EntityPlayer) entity;
        final double armorToughness = victim.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue();
        final float damage = CombatRules.getDamageAfterAbsorb(amount, victim.getTotalArmorValue(), (float) armorToughness);

        final boolean mortalWound = victim.getHealth() - damage <= 0;
        if (!mortalWound) return;

        if (event.isCancelable())
            event.setCanceled(true);

        if (battle.isTraining()) {
            victim.setHealth(CombatantProvider.get(victim).getTrainingHealth());

            victim.sendStatusMessage(new TextComponentString("u dead"), false);
            victim.inventory.damageArmor(damage);
        } else {
            victim.setHealth(0.5f);

            if (battle.isLeader(victim.getUniqueID()))
                Combat.MANAGER.finishTurn(battle);
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
