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
    private final Map<Long, Battle> battles = new HashMap<>();

    @Nullable
    public Battle getEntityBattle(EntityLivingBase entity) {
        if (entity == null) return null;
        return getBattle(CombatantProvider.get(entity).getBattleId());
    }

    @Nullable
    public Battle getBattle(long bid) {
        return battles.get(bid);
    }

    public void initBattle(EntityPlayer player, boolean training) {
        if (getEntityBattle(player) != null) return;

        final Battle battle = new Battle(training);
        battle.addMember(player);
        battles.put(battle.getId(), battle);

        final ICombatant com = CombatantProvider.get(player);
        com.setBattleId(battle.getId());

        CombatantSync.sync(player);
        BattleStateSync.sync(battle);
    }

    public void startBattle(EntityPlayer player) {
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

    public void finishTurn(Battle battle) {
        if (!battle.isTurnFinishing()) {
            BattleFlow.finishTurn(battle);
        }
    }

    public boolean tryNextTurn(Battle battle) {
        if (BattleFlow.selectNextLeader(battle)) {
            BattleFlow.prepareLeader(battle.getLeader());
            BattleStateSync.syncQueue(battle);
            BattleStateSync.syncDelay(battle);
            return true;
        } else {
            return false;
        }
    }

    public void repositionMembers(EntityLivingBase entity) {
        final Battle battle = getEntityBattle(entity);
        if (battle == null) return;

        battle.getCombatants().forEach(BattleFlow::repositionCombatant);
    }

    public void destroyBattle(Battle battle) {
        if (battle == null) return;

        battle.destroy();
        battles.remove(battle.getId());
        BattleStateSync.syncDestroy(battle);
    }

    public void addToBattle(long bid, EntityLivingBase entity) {
        final Battle battle = getBattle(bid);
        if (battle == null) return;

        final ICombatant com = CombatantProvider.get(entity);
        if (com.isInBattle()) return;

        battle.addMember(entity);
        if (battle.isStarted()) {
            battle.joinQueue(entity.getUniqueID());
            BattleFlow.engageEntity(entity);
        }
        BattleStateSync.sync(battle);

        com.setBattleId(bid);
        CombatantSync.sync(entity);
    }

    public void joinQueue(long bid, EntityLivingBase entity) {
        final Battle battle = getBattle(bid);
        if (battle == null) return;

        if (battle.isStarted() && !battle.isInQueue(entity.getUniqueID())) {
            battle.joinQueue(entity.getUniqueID());
            BattleFlow.engageEntity(entity);
            BattleStateSync.sync(battle);
        }
    }

    public void leaveQueue(long bid, EntityLivingBase entity) {
        final Battle battle = getBattle(bid);
        if (battle == null) return;

        if (battle.isStarted()) {
            battle.leaveQueue(entity.getUniqueID());
            BattleStateSync.sync(battle);
        }
    }

    public void exitBattle(EntityLivingBase entity) {
        final Battle battle = getEntityBattle(entity);
        if (battle == null) return;

        if (battle.isTraining())
            BattleFlow.restoreCombatantHealth(entity);
        BattleFlow.disengageEntity(entity);

        final boolean leaderRemoved = battle.isLeader(entity.getUniqueID());
        battle.removeMember(entity.getUniqueID());

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

    public void rejoinToBattle(EntityPlayerMP player) {
        final ICombatant com = CombatantProvider.get(player);
        if (!com.isInBattle()) return;

        final Battle battle = getBattle(com.getBattleId());
        if (battle != null) {
            battle.addMember(player);
//            BattleFlow.engageEntity(player);
            BattleStateSync.sync(player, battle);
        } else {
            com.reset();
            CombatantSync.sync(player);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
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
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
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
    public void onLivingDeath(LivingDeathEvent event) {
        exitBattle(event.getEntityLiving());
    }
}
