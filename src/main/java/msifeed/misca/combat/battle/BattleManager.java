package msifeed.misca.combat.battle;

import msifeed.misca.charsheet.CharsheetProvider;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.combat.cap.CombatantProvider;
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
    }

    public void startBattle(EntityPlayer player) {
        final Battle battle = getEntityBattle(player);
        if (battle == null || battle.isStarted()) return;

        battle.start();
        BattleStateSync.sync(battle);
    }

    public void repositionMembers(EntityLivingBase entity) {
        final Battle battle = getEntityBattle(entity);
        if (battle != null)
            battle.repositionMembers();
    }

    public void leaveFromBattle(EntityLivingBase entity) {
        final Battle battle = getEntityBattle(entity);
        if (battle == null) return;

        battle.removeMember(entity.getUniqueID());
        if (!battle.hasEnoughMembers()) {
            battle.clear();
            battles.remove(battle.getId());
        } else {
            BattleStateSync.sync(battle);
        }
    }

    public void destroyBattle(Battle battle) {
        battle.clear();
        battles.remove(battle.getId());
    }

    public void destroyBattle(EntityPlayer player) {
        final Battle battle = getEntityBattle(player);
        if (battle == null) return;

        battle.clear();
        battles.remove(battle.getId());
    }

    public void addToBattle(EntityPlayer player, EntityLivingBase newbie) {
        if (!(newbie instanceof EntityPlayer)) {
            final ICharsheet sheet = CharsheetProvider.get(newbie);
            sheet.attrs().setAll(5);
        }

        final Battle battle = getEntityBattle(player);
        if (battle == null) return;

        battle.addMember(newbie);
        BattleStateSync.sync(battle);
    }

    public void rejoinToBattle(EntityPlayerMP player) {
        final Battle battle = getEntityBattle(player);
        if (battle == null) return;

        battle.updateEntity(player);
        BattleStateSync.sync(player, battle);
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        leaveFromBattle(event.getEntityLiving());
    }
}
