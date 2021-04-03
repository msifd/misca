package msifeed.misca.combat.battle;

import msifeed.misca.combat.Combat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nullable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.stream.Stream;

public class Battle {
    private final long bid = new Random().nextInt(Short.MAX_VALUE) + 1;
    private final Map<UUID, WeakReference<EntityLivingBase>> members = new HashMap<>();
    private final Queue<UUID> queue = new ArrayDeque<>();
    private final boolean training;

    private int potionTickStart = 0;
    private long finishTurnDelayStart = 0;

    public Battle(boolean training) {
        this.training = training;
    }

    public long getId() {
        return bid;
    }

    public Map<UUID, WeakReference<EntityLivingBase>> getMembers() {
        return members;
    }

    @Nullable
    public EntityLivingBase getMember(@Nullable UUID key) {
        final WeakReference<EntityLivingBase> ref = members.get(key);
        return ref != null ? ref.get() : null;
    }

    public void addMember(EntityLivingBase entity) {
        members.put(entity.getUniqueID(), new WeakReference<>(entity));
    }

    public void removeMember(UUID entityId) {
        members.remove(entityId);
        queue.remove(entityId);
    }

    public void removeFromQueue(UUID entityId) {
        queue.remove(entityId);
    }

    public Stream<EntityLivingBase> getMemberEntities() {
        return members.values().stream()
                .map(Reference::get)
                .filter(Objects::nonNull);
    }

    public Stream<EntityPlayerMP> getPlayers() {
        return members.values().stream()
                .map(Reference::get)
                .filter(e -> e instanceof EntityPlayerMP)
                .map(e -> (EntityPlayerMP) e);
    }

    public boolean isLeader(UUID uuid) {
        return uuid.equals(getLeaderUuid());
    }

    @Nullable
    public UUID getLeaderUuid() {
        return queue.peek();
    }

    @Nullable
    public EntityLivingBase getLeader() {
        return getMember(getLeaderUuid());
    }

    public boolean isStarted() {
        return !queue.isEmpty();
    }

    public Queue<UUID> getQueue() {
        return queue;
    }

    public Stream<EntityLivingBase> getCombatants() {
        if (isStarted()) {
            return queue.stream()
                    .map(this::getMember)
                    .filter(Objects::nonNull);
        } else {
            return members.values().stream()
                    .map(Reference::get)
                    .filter(Objects::nonNull);
        }
    }

    @Nullable
    public EntityLivingBase getCombatantWithId(int id) {
        return getCombatants()
                .filter(e -> e.getEntityId() == id)
                .findAny()
                .orElse(null);
    }

    public boolean isInQueue(UUID entityId) {
        return queue.contains(entityId);
    }

    public void joinQueue(UUID entityId) {
        if (isStarted() && members.containsKey(entityId) && !queue.contains(entityId))
            queue.add(entityId);
    }

    public void leaveQueue(UUID entityId) {
        queue.remove(entityId);
    }

    public boolean isTraining() {
        return training;
    }

    public boolean shouldUpdatePotions(EntityLivingBase entity) {
        return isLeader(entity.getUniqueID())
                && entity.ticksExisted - potionTickStart < Combat.getRules().potionTicks;
    }

    public void resetPotionUpdateTick(EntityLivingBase entity) {
        potionTickStart = entity.ticksExisted;
    }

    public boolean isTurnFinishing() {
        return finishTurnDelayStart > 0;
    }

    public boolean isTurnDelayEnded() {
        return finishMoveDelayLeft() <= 0;
    }

    public long getFinishTurnDelay() {
        return finishTurnDelayStart;
    }

    public long finishMoveDelayLeft() {
        return Math.max(0, finishTurnDelayStart + Combat.getRules().finishTurnDelayMillis - System.currentTimeMillis());
    }

    public void setFinishTurnDelay(long time) {
        this.finishTurnDelayStart = time;
    }

    public void destroy() {
        if (isTraining())
            getMemberEntities().forEach(BattleFlow::restoreCombatantHealth);
        getMemberEntities().forEach(BattleFlow::disengageEntity);
    }

    public void clear() {
        members.clear();
        queue.clear();
    }

    public boolean shouldBeRemoved() {
        return members.isEmpty() || members.values().stream().allMatch(ref -> ref.get() == null);
    }
}
