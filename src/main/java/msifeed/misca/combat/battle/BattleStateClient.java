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
        newMembers.forEach(BattleStateClient::addMemberByUuid);

        // Cleanup removed members
        STATE.getMembers().entrySet().removeIf(e -> !newMembers.contains(e.getKey()));
    }

    public static void updateQueue(List<UUID> newQueue) {
        STATE.getQueue().clear();
        STATE.getQueue().addAll(newQueue);
        STATE.setFinishTurnDelay(0);

        checkMissingMembers();

        final EntityLivingBase leader = STATE.getLeader();
        if (leader != null) {
            STATE.resetPotionUpdateTick(leader);
        }
    }

    public static void checkMissingMembers() {
        // Find missing entities
        STATE.getMembers().forEach((uuid, ref) -> {
            if (ref.get() == null)
                addMemberByUuid(uuid);
        });
    }

    private static void addMemberByUuid(UUID uuid) {
        final World world = Minecraft.getMinecraft().world;
        world.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityLivingBase)
                .filter(entity -> entity.getUniqueID().equals(uuid))
                .findAny()
                .map(entity -> (EntityLivingBase) entity)
                .ifPresent(STATE::addMember);
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
