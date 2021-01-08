package msifeed.misca.combat.battle;

import msifeed.misca.charsheet.CharsheetProvider;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.combat.Combat;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.CombatantSync;
import msifeed.misca.combat.cap.ICombatant;
import msifeed.misca.combat.rules.Rules;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.lang.ref.WeakReference;
import java.util.*;

public class BattleFlow {
    //// Battle main lifecycle

    public static void formQueue(Battle battle) {
        final List<UUID> order = new ArrayList<>(battle.getMembers().keySet());
        Collections.shuffle(order);

        final Queue<UUID> queue = battle.getQueue();
        queue.clear();
        queue.addAll(order);
    }

    /**
     * Rotate queue until valid entity is found
     * @return <code>true</code> if valid leader was found, <code>false</code> otherwise
     */
    public static boolean selectNextLeader(Battle battle) {
        final Map<UUID, WeakReference<EntityLivingBase>> members = battle.getMembers();
        final Queue<UUID> queue = battle.getQueue();
        if (queue.size() < 2) return false;

        queue.add(queue.remove());

        // TODO: skip dead combatants
        for (int i = 0; i < queue.size(); i++) {
            final WeakReference<EntityLivingBase> leader = members.get(queue.peek());
            if (leader == null) queue.remove(); // Remove unknown member
            else if (leader.get() == null) queue.add(queue.remove()); // Rotate queue
            else return true;
        }

        return false;
    }

    public static void finishTurn(Battle battle) {
        final EntityLivingBase leader = battle.getLeader();
        if (leader == null) return;

        final ICharsheet cs = CharsheetProvider.get(leader);
        final ICombatant com = CombatantProvider.get(leader);
        com.setActionPoints(MathHelper.clamp(com.getActionPoints(), 0, Combat.getRules().maxActionPoints(cs)));
        com.setActionPointsOverhead(0);
        com.setPosition(leader.getPositionVector());
        CombatantSync.sync(leader);

        setMobAI(leader, false);
    }

    //// Combatants management

    public static boolean hasEnoughMembers(Battle battle) {
        return battle.getQueue().size() > 1 &&
                battle.getCombatants().anyMatch(e -> e instanceof EntityPlayer);
    }

    public static void engageEntity(EntityLivingBase entity) {
        final ICombatant com = CombatantProvider.get(entity);
        com.setActionPoints(0);
        com.setActionPointsOverhead(0);
        com.setPosition(entity.getPositionVector());
        CombatantSync.sync(entity);

        setMobAI(entity, false);
    }

    public static void disengageEntity(EntityLivingBase entity) {
        final ICombatant com = CombatantProvider.get(entity);
        com.reset();
        CombatantSync.sync(entity);

        setMobAI(entity, true);
    }

    public static void prepareLeader(EntityLivingBase entity) {
        final ICharsheet cs = CharsheetProvider.get(entity);
        final ICombatant com = CombatantProvider.get(entity);
        com.addActionPoints(Combat.getRules().actionPointsPerMove(cs));
        com.setPosition(entity.getPositionVector());
        CombatantSync.sync(entity);

//        final Vec3d pos = com.getPosition();
//        leader.setPositionAndUpdate(pos.x, pos.y, pos.z);
        setMobAI(entity, true);

//        if (leader instanceof EntityPlayer) {
//            final ITextComponent tc = new TextComponentString("your turn");
//            tc.getStyle().setColor(TextFormatting.GREEN);
//            ((EntityPlayer) leader).sendStatusMessage(tc, true);
//        }
    }

    public static void repositionCombatant(EntityLivingBase entity) {
        final Vec3d pos = CombatantProvider.get(entity).getPosition();
        entity.setPositionAndUpdate(pos.x, pos.y, pos.z);
    }

    public static void restoreCombatantHealth(EntityLivingBase entity) {
        final ICombatant com = CombatantProvider.get(entity);
        entity.setHealth(com.getTrainingHealth());
    }

    //// Action points management

    public static void consumeActionAp(EntityLivingBase entity) {
        final ICombatant com = CombatantProvider.get(entity);
        final double apWithOh = Combat.getRules().attackActionPoints(entity) + com.getActionPointsOverhead();
        if (com.getActionPoints() >= apWithOh) {
            com.addActionPoints(-apWithOh);
            com.setActionPointsOverhead(com.getActionPointsOverhead() + apWithOh / 2);
        }
    }

    public static void consumeUsageAp(EntityLivingBase entity) {
        final ICombatant com = CombatantProvider.get(entity);
        final double apWithOh = Combat.getRules().usageActionPoints() + com.getActionPointsOverhead();
        if (com.getActionPoints() >= apWithOh) {
            com.addActionPoints(-apWithOh);
            com.setActionPointsOverhead(com.getActionPointsOverhead() + apWithOh / 2);
        }
    }

    public static void consumeMovementAp(EntityLivingBase entity) {
        final ICombatant com = CombatantProvider.get(entity);
        final double ap = Combat.getRules().movementActionPoints(com.getPosition(), entity.getPositionVector());
        com.setPosition(entity.getPositionVector());
        com.setActionPoints(Math.max(com.getActionPoints() - ap, 0));
    }

    public static boolean isApDepleted(EntityLivingBase entity) {
        final ICombatant com = CombatantProvider.get(entity);
        final Rules rules = Combat.getRules();
        final double atk = rules.attackActionPoints(entity);
        final double use = rules.usageActionPoints();
        final double act = Math.min(atk, use) + com.getActionPointsOverhead();
        final double mov = rules.movementActionPoints(com.getPosition(), entity.getPositionVector());
        return (mov + act) > com.getActionPoints();
    }

    public static boolean isEnoughAp(EntityLivingBase entity, ICombatant com, double ap) {
        final Rules rules = Combat.getRules();
        final double act = ap + com.getActionPointsOverhead();
        final double mov = rules.movementActionPoints(com.getPosition(), entity.getPositionVector());
        return (mov + act) < com.getActionPoints();
    }

    //// Utils

    private static void setMobAI(EntityLivingBase entity, boolean enabled) {
        if (entity instanceof EntityLiving)
            ((EntityLiving) entity).setNoAI(!enabled);
    }
}
