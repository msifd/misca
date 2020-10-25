package msifeed.misca.combat.battle;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class BattleManager {
    private final HashSet<EntityLivingBase> candidates = new HashSet<>();
    private final HashMap<UUID, Battle> uuidToBattle = new HashMap<>();

    public boolean isCandidate(UUID entityId) {
        return candidates.stream().anyMatch(e -> e.getUniqueID().equals(entityId));
    }

    @Nullable
    public Battle getBattle(UUID entityId) {
        return uuidToBattle.get(entityId);
    }

    public void candidate(EntityLivingBase entity) {
        candidates.add(entity);
//        for (EntityLivingBase e : candidates) {
//
//        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        candidates.remove(event.getEntityLiving());

        final Battle battle = getBattle(event.getEntityLiving().getUniqueID());
        if (battle != null)
            battle.removeMember(event.getEntityLiving().getUniqueID());
    }
}
