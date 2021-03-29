package msifeed.misca.combat;

import msifeed.misca.combat.battle.Battle;
import msifeed.misca.combat.battle.BattleFlow;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.CombatantSync;
import msifeed.misca.combat.cap.ICombatant;
import msifeed.misca.combat.rules.WeaponInfo;
import net.minecraft.entity.EntityLivingBase;

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
}
