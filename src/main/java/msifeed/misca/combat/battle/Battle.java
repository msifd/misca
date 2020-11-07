package msifeed.misca.combat.battle;

import msifeed.misca.combat.cap.CombatantHandler;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.ICombatant;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.*;

public class Battle {
    private final UUID id = UUID.randomUUID();
    private final Map<UUID, WeakReference<EntityLivingBase>> members = new HashMap<>();
    private final Queue<UUID> queue = new ArrayDeque<>();
    private final boolean training;

    private BattlePhase phase = BattlePhase.INIT;
    private int actions;
    private long actionsStart;

    public Battle(boolean training) {
        this.training = training;
    }

    public UUID getId() {
        return id;
    }

    public boolean hasPlayerMember() {
        return members.values().stream()
                .map(Reference::get)
                .anyMatch(ref -> ref instanceof EntityPlayer);
    }

    public Collection<UUID> getMembers() {
        return members.keySet();
    }

    @Nullable
    public UUID getLeader() {
        return queue.isEmpty() ? null : queue.element();
    }

    public Collection<UUID> getQueue() {
        return queue;
    }

    public BattlePhase getPhase() {
        return phase;
    }

    public boolean isStarted() {
        return phase != BattlePhase.INIT;
    }

    public boolean isTraining() {
        return training;
    }

    public boolean isLeader(UUID uuid) {
        return !queue.isEmpty() && getLeader().equals(uuid);
    }

    public boolean isTurnExpired() {
        return actions >= 5 || (actionsStart > 0 && System.currentTimeMillis() - actionsStart > 2000);
    }

    public void start() {
        if (members.size() < 2) return;

        final List<UUID> order = new ArrayList<>(members.keySet());
        Collections.shuffle(order);
        queue.clear();
        queue.addAll(order);

        phase = BattlePhase.WAIT;
        actions = 0;
        actionsStart = 0;
    }

    public void makeAction() {
        phase = BattlePhase.ACTION;
        if (actions == 0)
            actionsStart = System.currentTimeMillis();
        actions++;

        if (isTurnExpired())
            finishTurn();
    }

    public void finishTurn() {
        final EntityLivingBase leader = members.get(getLeader()).get();
        if (leader instanceof EntityPlayer) {
            final ITextComponent tc = new TextComponentString("turn ended");
            tc.getStyle().setColor(TextFormatting.RED);
            ((EntityPlayer) leader).sendStatusMessage(tc, true);
        }

        queue.add(queue.remove());
        phase = BattlePhase.WAIT;
        actions = 0;
        actionsStart = 0;

        final EntityLivingBase newLeader = members.get(getLeader()).get();
        if (newLeader instanceof EntityPlayer) {
            final ITextComponent tc = new TextComponentString("your turn");
            tc.getStyle().setColor(TextFormatting.GREEN);
            ((EntityPlayer) newLeader).sendStatusMessage(tc, true);
        }
    }

    public void addMember(EntityLivingBase entity) {
        if (members.containsKey(entity.getUniqueID())) return;

        members.put(entity.getUniqueID(), new WeakReference<>(entity));
        if (!queue.isEmpty())
            queue.add(entity.getUniqueID());

        final ICombatant comb = CombatantProvider.get(entity);
        comb.setBattleId(id);

        if (training) {
            comb.setTrainingHealth(entity.getHealth());
        }

        CombatantHandler.sync(entity);
    }

    public void removeMember(UUID entityId) {
        final WeakReference<EntityLivingBase> removedRef = members.remove(entityId);
        final EntityLivingBase removed = removedRef != null ? removedRef.get() : null;

        queue.remove(entityId);

        if (removed != null && isTraining())
            restoreHealth(removed);

        if (isLeader(entityId))
            finishTurn();
    }

    public void clear() {
        if (isTraining()) {
            members.values().stream()
                    .map(Reference::get)
                    .filter(Objects::nonNull)
                    .forEach(this::restoreHealth);
        }

        members.clear();
        queue.clear();
    }

    private void restoreHealth(EntityLivingBase entity) {
        final ICombatant comb = CombatantProvider.get(entity);
        entity.setHealth(comb.getTrainingHealth());
        comb.setTrainingHealth(0);
        CombatantHandler.sync(entity);
    }
}
