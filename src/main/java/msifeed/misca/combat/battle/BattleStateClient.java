package msifeed.misca.combat.battle;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BattleStateClient {
    public static final Battle STATE = new Battle(false);

    public static void updateMembers(Set<UUID> newMembers) {
        final World world = Minecraft.getMinecraft().world;

        for (UUID uuid : newMembers) {
            final EntityLivingBase currEntity = STATE.getMember(uuid);
            if (currEntity != null) continue;

            // Lookup entity
            world.loadedEntityList.stream()
                    .filter(entity -> entity instanceof EntityLivingBase)
                    .filter(entity -> entity.getUniqueID().equals(uuid))
                    .findAny()
                    .map(entity -> (EntityLivingBase) entity)
                    .ifPresent(STATE::addMember);
        }

        // Cleanup removed members
        STATE.getMembers().entrySet().removeIf(e -> !newMembers.contains(e.getKey()));
    }

    public static void updateQueue(List<UUID> newQueue) {
        STATE.getQueue().clear();
        STATE.getQueue().addAll(newQueue);
        STATE.setFinishTurnDelay(0);

        final EntityLivingBase leader = STATE.getLeader();
        if (leader != null) {
            STATE.resetPotionUpdateTick(leader);
        }
    }

    public static void updateFinishDelay(long finishDelay) {
        STATE.setFinishTurnDelay(finishDelay);
    }

    public static void clear() {
        STATE.clear();
    }

    public static void fireUpdateEvent() {
        MinecraftForge.EVENT_BUS.post(new CombatUpdateEvent());
    }

    public static class CombatUpdateEvent extends Event {

    }
}
