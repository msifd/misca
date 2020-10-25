package msifeed.misca.combat.battle;

import net.minecraft.entity.EntityLivingBase;

import java.lang.ref.WeakReference;
import java.util.*;

public class Battle {
    private final Map<UUID, WeakReference<EntityLivingBase>> members = new HashMap<>();
    private final Queue<UUID> queue = new ArrayDeque<>();

    private int actions;
    private long actionsStart;

    public UUID getLeader() {
        return queue.element();
    }

    public boolean isLeader(UUID uuid) {
        return !queue.isEmpty() && getLeader().equals(uuid);
    }

    public boolean isTurnExpired() {
        return actions >= 5 || (actionsStart > 0 && System.currentTimeMillis() - actionsStart > 5000);
    }

    public void makeAction() {
        if (actions == 0)
            actionsStart = System.currentTimeMillis();
        actions++;

        if (isTurnExpired())
            finishTurn();
    }

    public void finishTurn() {
        queue.add(queue.remove());
        actions = 0;
        actionsStart = 0;
    }

    public void addMember(EntityLivingBase entity) {
        if (members.containsKey(entity.getUniqueID())) return;

        members.put(entity.getUniqueID(), new WeakReference<>(entity));
        if (!queue.isEmpty())
            queue.add(entity.getUniqueID());
    }

    public void removeMember(UUID entityId) {
        members.remove(entityId);
        queue.remove(entityId);

        if (isLeader(entityId))
            finishTurn();
    }

    public void start() {
        if (members.size() < 2) return;

        final List<UUID> order = new ArrayList<>(members.keySet());
        Collections.shuffle(order);
        queue.addAll(order);
    }
}
