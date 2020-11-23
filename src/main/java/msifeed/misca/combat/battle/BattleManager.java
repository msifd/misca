package msifeed.misca.combat.battle;

import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.charsheet.cap.ICharsheet;
import msifeed.misca.combat.cap.CombatantProvider;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class BattleManager {
    private final Map<Long, Battle> battles = new HashMap<>();

    @Nullable
    public Battle getEntityBattle(EntityLivingBase entity) {
        return getBattle(CombatantProvider.get(entity).getBattleId());
    }

    @Nullable
    public Battle getBattle(long bid) {
        return battles.get(bid);
    }

    public void initBattle(EntityPlayer player, boolean training) {
        if (getEntityBattle(player) != null) return;

        final Battle b = new Battle(training);
        b.addMember(player);

        battles.put(b.getId(), b);
    }

    public void startBattle(EntityPlayer player) {
        final Battle battle = getEntityBattle(player);
//        if (battle != null && !battle.isStarted())
        if (battle != null)
            battle.start();
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
        if (!battle.hasPlayerMember()) {
            battle.clear();
            battles.remove(battle.getId());
        }
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
        if (battle != null)
            battle.addMember(newbie);
    }

    public void rejoinToBattle(EntityPlayer player) {
        final Battle battle = getEntityBattle(player);
        if (battle != null)
            battle.updateEntity(player);
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        leaveFromBattle(event.getEntityLiving());
    }
}
