package msifeed.misca.combat.battle;

import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.CombatantSync;
import msifeed.misca.combat.cap.ICombatant;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BattleManager {
    private static final Map<Long, Battle> battles = new HashMap<>();

    @Nullable
    public static Battle getEntityBattle(EntityLivingBase entity) {
        if (entity == null) return null;
        return getBattle(CombatantProvider.get(entity).getBattleId());
    }

    @Nullable
    public static Battle getBattle(long bid) {
        return battles.get(bid);
    }

    public static void initBattle(EntityPlayer player, boolean training) {
        if (getEntityBattle(player) != null) return;

        final Battle battle = new Battle(training);
        battle.addMember(player);
        battles.put(battle.getId(), battle);

        final ICombatant com = CombatantProvider.get(player);
        com.setBattleId(battle.getId());
        if (training)
            com.setTrainingHealth(player.getHealth());

        CombatantSync.sync(player);
        BattleStateSync.sync(battle);
    }

    public static void startBattle(EntityPlayer player) {
        final Battle battle = getEntityBattle(player);
        if (battle == null || battle.isStarted()) return;

        BattleFlow.formQueue(battle);
        if (BattleFlow.selectNextLeader(battle)) {
            BattleFlow.prepareLeader(battle.getLeader());
            BattleStateSync.sync(battle);
        } else {
            destroyBattle(battle);
        }
    }

    public static void finishTurn(Battle battle) {
        if (!battle.isTurnFinishing()) {
            BattleFlow.finishTurn(battle);
        }
    }

    public static boolean tryNextTurn(Battle battle) {
        if (BattleFlow.selectNextLeader(battle)) {
            BattleFlow.prepareLeader(battle.getLeader());
            BattleStateSync.syncQueue(battle);
            BattleStateSync.syncDelay(battle);
            return true;
        } else {
            return false;
        }
    }

    public static void repositionMembers(EntityLivingBase entity) {
        final Battle battle = getEntityBattle(entity);
        if (battle == null) return;

        battle.getCombatants().forEach(BattleFlow::repositionCombatant);
    }

    public static void destroyBattle(Battle battle) {
        if (battle == null) return;

        battle.destroy();
        battles.remove(battle.getId());
        BattleStateSync.syncDestroy(battle);

        System.out.println("battle destroyed " + battles.size());
    }

    public static void addToBattle(long bid, EntityLivingBase entity) {
        final Battle battle = getBattle(bid);
        if (battle == null) return;

        final ICombatant com = CombatantProvider.get(entity);
        if (com.isInBattle()) return;

        if (battle.isTraining())
            com.setTrainingHealth(entity.getHealth());

        battle.addMember(entity);
        if (battle.isStarted()) {
            battle.joinQueue(entity.getUniqueID());
            BattleFlow.engageEntity(entity);
        }
        BattleStateSync.sync(battle);

        com.setBattleId(bid);
        CombatantSync.sync(entity);
    }

    public static void joinQueue(long bid, EntityLivingBase entity) {
        final Battle battle = getBattle(bid);
        if (battle == null) return;

        if (battle.isStarted() && !battle.isInQueue(entity.getUniqueID())) {
            battle.joinQueue(entity.getUniqueID());
            BattleFlow.engageEntity(entity);
            BattleStateSync.sync(battle);
        }
    }

    public static void leaveQueue(long bid, EntityLivingBase entity) {
        final Battle battle = getBattle(bid);
        if (battle == null) return;

        if (battle.isStarted()) {
            battle.leaveQueue(entity.getUniqueID());
            BattleStateSync.sync(battle);
        }
    }

    public static void exitBattle(EntityLivingBase entity) {
        final Battle battle = getEntityBattle(entity);
        if (battle == null) return;

        if (battle.isTraining())
            BattleFlow.restoreCombatantHealth(entity);
        BattleFlow.disengageEntity(entity);

        final boolean leaderRemoved = battle.isLeader(entity.getUniqueID());
        battle.removeMember(entity.getUniqueID());
        if (entity instanceof EntityPlayerMP)
            BattleStateSync.sync((EntityPlayerMP) entity, battle);

        if (leaderRemoved) {
            if (BattleFlow.selectNextLeader(battle)) {
                BattleFlow.prepareLeader(battle.getLeader());
            } else {
                destroyBattle(battle);
                return;
            }
        }

        if (BattleFlow.hasEnoughMembers(battle)) {
            BattleStateSync.sync(battle);
        } else {
            destroyBattle(battle);
        }
    }

    public static void rejoinToBattle(EntityPlayerMP player) {
        final ICombatant com = CombatantProvider.get(player);
        if (!com.isInBattle()) return;

        final Battle battle = getBattle(com.getBattleId());
        if (battle != null) {
            battle.addMember(player);
//            BattleFlow.engageEntity(player);
            BattleStateSync.sync(player, battle);
        } else {
            com.reset();
        }

        CombatantSync.sync(player);
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ServerTickEvent event) {
        final Iterator<Battle> it = battles.values().iterator();
        while (it.hasNext()) {
            final Battle battle = it.next();

            if (battle.isTurnFinishing() && battle.isTurnDelayEnded()) {
                battle.setFinishTurnDelay(0);
                if (!tryNextTurn(battle)) {
                    battle.destroy();
                    battle.clear();
                    it.remove();
                }
            } else if (battle.shouldBeRemoved()) {
                battle.destroy();
                battle.clear();
                it.remove();
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!(event.getEntity() instanceof EntityLivingBase)) return;
        if (event.getEntity().world.isRemote) return;

        final EntityLivingBase target = (EntityLivingBase) event.getEntity();
        final ICombatant com = CombatantProvider.get(target);
        if (!com.isInBattle()) return;

        final Battle battle = getBattle(com.getBattleId());
        if (battle == null) {
            com.reset();
            CombatantSync.sync(target);
        } else if (target instanceof EntityPlayerMP) {
            rejoinToBattle((EntityPlayerMP) target);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        final Battle battle = getEntityBattle(event.getEntityLiving());
        if (battle == null) return;

        if (battle.isTraining()) {
            event.setCanceled(true);
        } else {
            exitBattle(event.getEntityLiving());
        }
    }
}
