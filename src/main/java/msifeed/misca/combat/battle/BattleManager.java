package msifeed.misca.combat.battle;

import msifeed.misca.charsheet.CharsheetProvider;
import msifeed.misca.charsheet.CharsheetSync;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.CombatantSync;
import msifeed.misca.combat.cap.ICombatant;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
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
        BattleStateSync.sync(battle);

        final ICombatant com = CombatantProvider.get(player);
        com.setBattleId(battle.getId());
        CombatantSync.sync(player);
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

    public void nextTurn(Battle battle) {
        BattleFlow.finishTurn(battle);
        if (BattleFlow.selectNextLeader(battle)) {
            BattleFlow.prepareLeader(battle.getLeader());
            BattleStateSync.syncQueue(battle);
        } else {
            destroyBattle(battle);
        }
    }

    public void repositionMembers(EntityLivingBase entity) {
        final Battle battle = getEntityBattle(entity);
        if (battle == null) return;

        battle.getCombatants().forEach(BattleFlow::repositionCombatant);
    }

    public void destroyBattle(Battle battle) {
        if (battle == null) return;

        if (battle.isTraining())
            battle.getMemberEntities().forEach(BattleFlow::restoreCombatantHealth);
        battle.getMemberEntities().forEach(BattleFlow::disengageEntity);

        battle.clear();
        battles.remove(battle.getId());
    }

    public void addToBattle(long bid, EntityLivingBase entity) {
        final Battle battle = getBattle(bid);
        if (battle == null) return;

        battle.addMember(entity);
        if (battle.isStarted()) {
            battle.joinQueue(entity.getUniqueID());
            BattleFlow.engageEntity(entity);
        }
        BattleStateSync.sync(battle);

        final ICombatant com = CombatantProvider.get(entity);
        com.setBattleId(bid);
        CombatantSync.sync(entity);

        if (!(entity instanceof EntityPlayer)) {
            final ICharsheet cs = CharsheetProvider.get(entity);
            cs.attrs().setAll(5);
            CharsheetSync.sync(entity);
        }
    }

    public void leaveFromBattle(EntityLivingBase entity) {
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
            BattleFlow.engageEntity(player);
            BattleStateSync.sync(player, battle);
        } else {
            com.reset();
        }

        CombatantSync.sync(player);
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        leaveFromBattle(event.getEntityLiving());
    }
}
