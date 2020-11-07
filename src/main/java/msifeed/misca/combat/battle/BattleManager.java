package msifeed.misca.combat.battle;

import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.charsheet.cap.ICharsheet;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.ICombatant;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class BattleManager {
    private final HashMap<UUID, Battle> battles = new HashMap<>();

    public Optional<Battle> getEntityBattle(EntityLivingBase entity) {
        final UUID bid = CombatantProvider.get(entity).getBattleId();
        return Optional.ofNullable(battles.get(bid));
    }

    public void initBattle(EntityPlayer player, boolean training) {
        if (getEntityBattle(player).isPresent()) return;

        final Battle b = new Battle(training);
        b.addMember(player);

        battles.put(b.getId(), b);
    }

    public void startBattle(EntityPlayer player) {
        getEntityBattle(player)
                .filter(b -> !b.isStarted())
                .ifPresent(Battle::start);
    }

    public void leaveFromBattle(EntityLivingBase entity) {
        final Battle battle = getEntityBattle(entity).orElse(null);
        if (battle == null) return;

        battle.removeMember(entity.getUniqueID());
        if (!battle.hasPlayerMember()) {
            battle.clear();
            battles.remove(battle.getId());
        }
    }

    public void destroyBattle(EntityPlayer player) {
        getEntityBattle(player).ifPresent(battle -> {
            battle.clear();
            battles.remove(battle.getId());
        });
    }

    public void addToBattle(EntityPlayer player, EntityLivingBase newbie) {
        if (!(newbie instanceof EntityPlayer)) {
            final ICharsheet sheet = CharsheetProvider.get(newbie);
            sheet.attrs().setAll(5);
        }

        getEntityBattle(player).ifPresent(battle -> {
            battle.addMember(newbie);
        });
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        leaveFromBattle(event.getEntityLiving());
    }
}
