package msifeed.misca.combat;

import msifeed.misca.combat.battle.Battle;
import msifeed.misca.combat.battle.BattleFlow;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.CombatantSync;
import msifeed.misca.combat.cap.ICombatant;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;

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

    public static boolean canUse(EntityLivingBase actor, Item item) {
        final ICombatant com = CombatantProvider.get(actor);
        if (!com.isInBattle()) return true;

        final Battle battle = Combat.MANAGER.getBattle(com.getBattleId());
        if (battle == null) return true;

        if (!battle.isLeader(actor.getUniqueID())) return false;
        if (battle.isTurnFinishing()) return false;

        final double usageAp = Combat.getRules().usageActionPoints(item);
        return BattleFlow.isEnoughAp(actor, com, usageAp);
    }

    public static void onUse(EntityLivingBase actor, Item item) {
        BattleFlow.consumeUsageAp(actor, item);
        BattleFlow.consumeMovementAp(actor);
        CombatantSync.sync(actor);
    }

    // Attack

    public static boolean trySourceAttack(EntityLivingBase source, Item weapon) {
        final EntityLivingBase actor = getCombatActor(source);
        if (actor == null) return true;

        if (canAttack(actor, weapon)) {
            onAttack(actor, weapon);
            return true;
        } else {
            return false;
        }
    }

    public static void onSourceAttack(EntityLivingBase source) {
        final EntityLivingBase actor = getCombatActor(source);
        if (actor == null) return;

        final Item weapon = source.getHeldItemMainhand().getItem();
        onAttack(actor, weapon);
    }

    public static boolean canAttack(EntityLivingBase actor, Item item) {
        final ICombatant com = CombatantProvider.get(actor);
        if (!com.isInBattle()) return true;

        final Battle battle = Combat.MANAGER.getBattle(com.getBattleId());
        if (battle == null) return true;

        if (!battle.isLeader(actor.getUniqueID())) return false;
        if (battle.isTurnFinishing()) return false;

        return isEnoughAttackAp(actor, item);
    }

    public static boolean isEnoughAttackAp(EntityLivingBase actor, Item item) {
        final double attackAp = Combat.getRules().attackActionPoints(actor, item);
        return BattleFlow.isEnoughAp(actor, CombatantProvider.get(actor), attackAp);
    }

    public static void onAttack(EntityLivingBase actor, Item item) {
        BattleFlow.consumeActionAp(actor, item);
        BattleFlow.consumeMovementAp(actor);
        CombatantSync.sync(actor);

        if (BattleFlow.isApDepleted(actor, item)) {
            final ICombatant com = CombatantProvider.get(actor);
            final Battle battle = Combat.MANAGER.getBattle(com.getBattleId());
            if (battle != null)
                Combat.MANAGER.finishTurn(battle);
        }
    }
}