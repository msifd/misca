package msifeed.misca.combat;

import msifeed.misca.combat.battle.Battle;
import msifeed.misca.combat.battle.BattleFlow;
import msifeed.misca.combat.battle.BattleManager;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.CombatantSync;
import msifeed.misca.combat.cap.ICombatant;
import msifeed.misca.combat.rules.WeaponInfo;
import msifeed.misca.combat.rules.WeaponTrait;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CombatHandler {

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
        final Battle battle = BattleManager.getBattle(com.getBattleId());
        if (battle == null || !battle.isLeader(entity.getUniqueID())) return;

        final double movementAp = Combat.getRules().movementActionPoints(entity, com.getPosition(), entity.getPositionVector());
        if (com.getActionPoints() <= 0 || com.getActionPoints() < movementAp) {
            BattleFlow.consumeMovementAp(entity);
            CombatantSync.syncAp(entity);
            BattleManager.finishTurn(battle);
        }
    }

    @SubscribeEvent
    public void onPlayerAttackEmptySpace(PlayerInteractEvent.LeftClickEmpty event) {
        if (event.getEntityLiving().world.isRemote) return;

        final EntityLivingBase source = event.getEntityLiving();
        final EntityLivingBase actor = CombatFlow.getCombatActor(source);
        if (actor == null) return;

        final ItemStack stack = event.getEntityPlayer().getHeldItemMainhand();
        final WeaponInfo weapon = Combat.getWeapons().get(source, stack);
        if (CombatFlow.canAttack(actor, weapon)) {
            CombatFlow.onAttack(actor, weapon);
        }
    }

    @SubscribeEvent
    public void onPlayerAttackBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getEntityLiving().world.isRemote) return;

        final EntityLivingBase source = event.getEntityLiving();
        final EntityLivingBase actor = CombatFlow.getCombatActor(source);
        if (actor == null) return;

        final ItemStack stack = event.getEntityPlayer().getHeldItemMainhand();
        if (stack.getItem() instanceof ItemCombatTool) return;

        final WeaponInfo weapon = Combat.getWeapons().get(source, stack);
        if (CombatFlow.canAttack(actor, weapon)) {
            CombatFlow.onAttack(actor, weapon);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerAttackEntity(AttackEntityEvent event) {
        if (event.getEntityLiving().world.isRemote || event.isCanceled()) return;

        final EntityLivingBase source = event.getEntityLiving();
        final EntityLivingBase actor = CombatFlow.getCombatActor(source);
        if (actor == null) return;

        final ItemStack stack = event.getEntityPlayer().getHeldItemMainhand();
        if (stack.getItem() instanceof ItemCombatTool) return;

        final WeaponInfo weapon = Combat.getWeapons().get(source, stack);
        if (CombatFlow.canAttack(actor, weapon)) {
            CombatFlow.onAttack(actor, weapon);
        } else {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onAttack(LivingAttackEvent event) {
        if (!(event.getSource() instanceof EntityDamageSource)) return;
        if (!CombatFlow.isAttackIgnored(event.getSource())) return;

        final EntityDamageSource src = (EntityDamageSource) event.getSource();
        if (!(src.getTrueSource() instanceof EntityLivingBase)) return;

        final EntityLivingBase source = (EntityLivingBase) src.getTrueSource();
        // Get weapon from source, not from actor
        final WeaponInfo weapon = Combat.getWeapons().get(source, source.getHeldItemMainhand());

        final EntityLivingBase actor = CombatFlow.getCombatActor(source);
        if (actor == null) return;

        if (CombatFlow.canNotTakeDamage(actor)) {
            event.setCanceled(true);
            return;
        }

        if (CombatFlow.canAttack(actor, weapon)) {
            final boolean isPlayer = source instanceof EntityPlayer; // Players consume AP on swing, mobs on attack
            if (!isPlayer) {
                CombatFlow.onAttack(actor, weapon);
            }
        } else {
            if (!CombatFlow.canDamage(actor)) {
                event.setCanceled(true);
            }

            // Mobs cant finish their turn, so lets help them
            final ICombatant com = CombatantProvider.get(actor);
            final Battle battle = BattleManager.getBattle(com.getBattleId());
            if (battle != null && battle.isLeader(actor.getUniqueID()))
                BattleManager.finishTurn(battle);
        }

        if (BattleFlow.isApDepleted(actor, weapon)) {
            final ICombatant com = CombatantProvider.get(actor);
            final Battle battle = BattleManager.getBattle(com.getBattleId());
            if (battle != null)
                BattleManager.finishTurn(battle);
        }
    }

    @SubscribeEvent
    public void onHurt(LivingHurtEvent event) {
        // Neutral damage has no rolls
        if (!(event.getSource() instanceof EntityDamageSource)) {
            CombatFlow.handleNeutralDamage(event);
            return;
        }

        final EntityDamageSource damage = (EntityDamageSource) event.getSource();
        if (!(damage.getTrueSource() instanceof EntityLivingBase)) return;

        final EntityLivingBase source = (EntityLivingBase) damage.getTrueSource();
        if (source.world.isRemote) return;

        final EntityLivingBase victim = event.getEntityLiving();
        if (victim.world.isRemote) return;

        final ICombatant srcCom = CombatantProvider.get(source);
        if (!srcCom.isInBattle()) {
            handleNonCombatDamage(event, source);
            return;
        }

        final EntityLivingBase actor = CombatFlow.getCombatActor(source);
        if (actor == null) return;

        if (CombatFlow.isAttackIgnored(damage)) {
            // Get weapon from source, not from actor
            final WeaponInfo weapon = Combat.getWeapons().get(source, source.getHeldItemMainhand());
            CombatFlow.alterDamage(event, source, actor, weapon);
        }

        final Battle battle = BattleManager.getBattle(srcCom.getBattleId());
        CombatFlow.handleDeadlyAttack(event, event.getAmount(), victim, battle);
    }

    private void handleNonCombatDamage(LivingHurtEvent event, EntityLivingBase source) {
        if (!(source instanceof EntityPlayer)) return;

        final EntityLivingBase victim = event.getEntityLiving();

        // Get weapon from source, not from actor
        final WeaponInfo weapon = Combat.getWeapons().get(source, source.getHeldItemMainhand());
        if (victim instanceof EntityPlayer) {
            event.setAmount(event.getAmount() * Combat.getRules().damageFactor(source, victim, weapon));
        } else {
            event.setAmount(event.getAmount() * Combat.getRules().damageIncreaseFactor(source, weapon));
        }
    }

    @SubscribeEvent
    public void onItemUse(PlayerInteractEvent.RightClickItem event) {
        final ItemStack stack = event.getItemStack();
        final WeaponInfo weapon = Combat.getWeapons().get(event.getEntityLiving(), stack);

        if (!weapon.has(WeaponTrait.canUse)) return;
        if (stack.getItemUseAction() == EnumAction.NONE && weapon.traits.isEmpty()) return;

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
        final WeaponInfo weapon = Combat.getWeapons().get(event.getEntityLiving(), event.getItem());
        if (!weapon.has(WeaponTrait.canHoldUse)) return;

        final EntityLivingBase actor = CombatFlow.getCombatActor(event.getEntityLiving());
        if (actor == null) return;

        if (!CombatFlow.canUse(actor, weapon)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onItemUseStop(LivingEntityUseItemEvent.Stop event) {
        if (event.getEntityLiving().world.isRemote) return;

        final WeaponInfo weapon = Combat.getWeapons().get(event.getEntityLiving(), event.getItem());
        if (!weapon.has(WeaponTrait.canHoldUse)) return;

        final EntityLivingBase actor = CombatFlow.getCombatActor(event.getEntityLiving());
        if (actor == null) return;

        CombatFlow.onUse(actor, weapon);
    }

    @SubscribeEvent
    public void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntityLiving().world.isRemote) return;

        final WeaponInfo weapon = Combat.getWeapons().get(event.getEntityLiving(), event.getItem());
        if (!weapon.has(WeaponTrait.canHoldUse)) return;

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

        final WeaponInfo weapon = Combat.getWeapons().getItemInfo(Items.SPLASH_POTION);
        if (CombatFlow.canAttack(actor, weapon)) {
            CombatFlow.onAttack(actor, weapon);
        } else {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onKnockBack(LivingKnockBackEvent event) {
        if (CombatFlow.attackEvaded) {
            CombatFlow.attackEvaded = false;
            event.setCanceled(true);
        }
    }
}
