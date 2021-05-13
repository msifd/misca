package msifeed.misca.combat;

import msifeed.misca.combat.battle.Battle;
import msifeed.misca.combat.battle.BattleFlow;
import msifeed.misca.combat.battle.BattleStateSync;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.CombatantSync;
import msifeed.misca.combat.cap.ICombatant;
import msifeed.misca.combat.rules.CombatantInfo;
import msifeed.misca.combat.rules.Rules;
import msifeed.misca.combat.rules.WeaponInfo;
import msifeed.misca.rolls.Dices;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;

public class CombatFlow {
    /**
     * @param entity - action source
     * @return null if entity is not in combat, entity's puppet or param entity otherwise.
     */
    @Nullable
    public static EntityLivingBase getCombatActor(EntityLivingBase entity) {
        if (entity == null) return null;

        final ICombatant srcCom = CombatantProvider.get(entity);
        if (!srcCom.isInBattle()) return null;

        final Battle battle = Combat.MANAGER.getBattle(srcCom.getBattleId());
        if (battle == null) return null;

        if (srcCom.hasPuppet()) {
            final EntityLivingBase pup = battle.getCombatantWithId(srcCom.getPuppet());
            if (pup != null)
                return pup;
        }

        return entity;
    }

    // Usage

    public static boolean canUse(EntityLivingBase actor, WeaponInfo weapon) {
        final ICombatant com = CombatantProvider.get(actor);
        if (!com.isInBattle()) return true;

        final Battle battle = Combat.MANAGER.getBattle(com.getBattleId());
        if (battle == null) return true;

        if (!battle.isLeader(actor.getUniqueID())) return false;
        if (battle.isTurnFinishing()) return false;

        final double usageAp = Combat.getRules().usageActionPoints(weapon);
        return BattleFlow.isEnoughAp(actor, com, usageAp);
    }

    public static void onUse(EntityLivingBase actor, WeaponInfo weapon) {
        if (actor.world.isRemote) return;

        BattleFlow.consumeUsageAp(actor, weapon);
        BattleFlow.consumeMovementAp(actor);
        CombatantSync.syncAp(actor);
    }

    // Attack

    public static boolean canDamage(EntityLivingBase actor) {
        final ICombatant com = CombatantProvider.get(actor);
        if (!com.isInBattle()) return true;

        final Battle battle = Combat.MANAGER.getBattle(com.getBattleId());
        if (battle == null) return true;

        return battle.isLeader(actor.getUniqueID());
    }

    public static boolean canAttack(EntityLivingBase actor, WeaponInfo weapon) {
        final ICombatant com = CombatantProvider.get(actor);
        if (!com.isInBattle()) return true;

        final Battle battle = Combat.MANAGER.getBattle(com.getBattleId());
        if (battle == null) return true;

        if (!battle.isLeader(actor.getUniqueID())) return false;
        if (battle.isTurnFinishing()) return false;

        final double attackAp = Combat.getRules().attackActionPoints(actor, weapon);
        return BattleFlow.isEnoughAp(actor, CombatantProvider.get(actor), attackAp);
    }

    public static void onAttack(EntityLivingBase actor, WeaponInfo weapon) {
        if (actor.world.isRemote) return;

        BattleFlow.consumeActionAp(actor, weapon);
        BattleFlow.consumeMovementAp(actor);
        CombatantSync.syncAp(actor);

        if (BattleFlow.isApDepleted(actor, weapon)) {
            final ICombatant com = CombatantProvider.get(actor);
            final Battle battle = Combat.MANAGER.getBattle(com.getBattleId());
            if (battle != null)
                Combat.MANAGER.finishTurn(battle);
        }
    }

    // Damage

    private static final String IGNORE_PREFIX = "ignore-";
    private static final String CRIT_EVASION_DAMAGE_TYPE = IGNORE_PREFIX + "evasion";
    public static final DamageSource NEUTRAL_DAMAGE = new DamageSource(IGNORE_PREFIX + "neutral");

    public static boolean isAttackIgnored(DamageSource source) {
        return !source.getDamageType().startsWith(IGNORE_PREFIX);
    }

    public static boolean canNotTakeDamage(EntityLivingBase leader) {
        if (leader == null) return false;
        if (!(leader instanceof EntityPlayer)) return false;

        final ICombatant com = CombatantProvider.get(leader);
        return com.getActionPointsSpent() <= 0;
    }

    /**
     * Shitty hack to disable knock back after evasion
     */
    public static boolean attackEvaded = false;

    public static void alterDamage(LivingHurtEvent event, EntityLivingBase actor, WeaponInfo weapon) {
        final float damageAmount = event.getAmount() + weapon.dmg;
        if (damageAmount <= 0) {
            event.setCanceled(true);
            return;
        }

        final EntityLivingBase victim = event.getEntityLiving();
        final CombatantInfo actInfo = new CombatantInfo(actor, event.getSource(), weapon);
        final CombatantInfo vicInfo = new CombatantInfo(victim);
        final Rules rules = Combat.getRules();

        final Rules.AttackResult result = rules.rollAttack(actInfo, vicInfo);
        if (result.isSuccessful()) {
            float damageFactor = rules.damageFactor(actor, victim, weapon);

            if (result.isCritHit()) {
                damageFactor += 1;
            } else if (rules.isCloseRangeMagic(actInfo, victim) && Dices.check(rules.magicCloseRangeSpreadChance)) {
                final float selfDamage = damageAmount * rules.damageFactor(actor, actor, weapon);
                actor.attackEntityFrom(event.getSource(), selfDamage);

                if (Dices.check(rules.magicCloseRangeMissChance)) {
                    damageFactor = 0;
                    event.setCanceled(true);
                }
            }

            event.setAmount(damageAmount * damageFactor);
        } else {
            attackEvaded = true;
            event.setCanceled(true);

            if (result.isCritEvade()) {
                final DamageSource ds = new EntityDamageSource(CRIT_EVASION_DAMAGE_TYPE, victim);
                final float selfDamage = damageAmount * rules.damageFactor(actor, actor, weapon);
                actor.attackEntityFrom(ds, selfDamage);
            }
        }
    }

    public static void handleDeadlyAttack(Event event, float amount, EntityLivingBase entity, Battle battle) {
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
}
