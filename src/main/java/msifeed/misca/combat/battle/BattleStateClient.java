package msifeed.misca.combat.battle;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.*;

@SideOnly(Side.CLIENT)
public enum BattleStateClient {
    INSTANCE;

    private final Map<UUID, WeakReference<EntityLivingBase>> members = new HashMap<>();
    private final List<UUID> queue = new ArrayList<>();

    public UUID getLeaderUuid() {
        return !queue.isEmpty() ? queue.get(0) : null;
    }

    public List<UUID> getQueue() {
        return queue;
    }

    public Map<UUID, WeakReference<EntityLivingBase>> getMembers() {
        return members;
    }

    @Nullable
    public WeakReference<EntityLivingBase> getMember(UUID key) {
        return members.get(key);
    }

    public void updateMembers(Set<UUID> newMembers) {
        final World world = Minecraft.getMinecraft().world;

        for (UUID uuid : newMembers) {
            final WeakReference<EntityLivingBase> currEntity = getMember(uuid);
            if (currEntity != null && currEntity.get() != null) continue;

            // Lookup entity
            world.loadedEntityList.stream()
                    .filter(entity -> entity instanceof EntityLivingBase)
                    .filter(entity -> entity.getUniqueID().equals(uuid))
                    .findAny()
                    .map(entity -> (EntityLivingBase) entity)
                    .ifPresent(entity -> {
                        this.members.put(uuid, new WeakReference<>(entity));
                    });
        }

        // Cleanup removed members
        this.members.entrySet().removeIf(e -> !newMembers.contains(e.getKey()));
    }

    public void updateQueue(List<UUID> newQueue) {
        this.queue.clear();
        this.queue.addAll(newQueue);
    }

    public void clear() {
        this.members.clear();
        this.queue.clear();
    }
}
