package msifeed.misca.combat.battle;

import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.charsheet.cap.ICharsheet;
import msifeed.misca.combat.Combat;
import msifeed.misca.combat.cap.CombatantHandler;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.ICombatant;
import msifeed.misca.combat.rules.Rules;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.*;

public class Battle {
    private final long bid;
    private final Map<UUID, WeakReference<EntityLivingBase>> members = new HashMap<>();
    private final Queue<UUID> queue = new ArrayDeque<>();
    private final boolean training;

    private BattlePhase phase = BattlePhase.INIT;
    private int actions;
    private long actionsStart;

    public Battle(boolean training) {
        final Random rand = new Random();
        long id = rand.nextLong();
        while (id == 0)
            id = rand.nextLong();

        this.bid = id;
        this.training = training;
    }

    public long getId() {
        return bid;
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
        return uuid.equals(getLeader());
    }

    public void start() {
        if (members.size() < 2) return;

        final List<UUID> order = new ArrayList<>(members.keySet());
        Collections.shuffle(order);
        queue.clear();
        queue.addAll(order);

        queue.stream()
                .map(members::get)
                .map(Reference::get)
                .filter(Objects::nonNull)
                .filter(e -> !isLeader(e.getUniqueID()))
                .forEach(entity -> {
                    final ICombatant com = CombatantProvider.get(entity);
                    com.setActionPoints(5);
                    com.setActionPointsOverhead(0);
                    com.setPosition(entity.getPositionVector());
                    CombatantHandler.sync(entity);
                });

        phase = BattlePhase.WAIT;
        actions = 0;
        actionsStart = 0;
    }

    public void repositionMembers() {
        queue.stream()
                .map(members::get)
                .map(Reference::get)
                .filter(Objects::nonNull)
                .forEach(entity -> {
                    final Vec3d pos = CombatantProvider.get(entity).getPosition();
                    entity.setPositionAndUpdate(pos.x, pos.y, pos.z);
                });
    }

    public void makeAction() {
        phase = BattlePhase.ACTION;
        if (actions == 0)
            actionsStart = System.currentTimeMillis();
        actions++;

        final EntityLivingBase leader = members.get(getLeader()).get();
        if (leader != null) {
            final ICharsheet cs = CharsheetProvider.get(leader);
            final ICombatant com = CombatantProvider.get(leader);
            consumeActionAp(leader, com);
            consumeMovementAp(leader, com);
            com.setActionPoints(MathHelper.clamp(com.getActionPoints(), 0, Rules.maxActionPoints(cs)));

            final boolean turnExpired = actions >= 5
                    || (actionsStart > 0 && System.currentTimeMillis() - actionsStart > 2000)
                    || com.getActionPoints() == 0;

            if (turnExpired)
                finishTurn();

            CombatantHandler.sync(leader);
        } else {
            finishTurn();
        }
    }

    public void finishTurn() {
        final EntityLivingBase leader = members.get(getLeader()).get();
        if (leader != null) {
            CombatantProvider.get(leader).setActionPointsOverhead(0);
        }
        if (leader instanceof EntityPlayer) {
            final ITextComponent tc = new TextComponentString("turn ended");
            tc.getStyle().setColor(TextFormatting.RED);
            ((EntityPlayer) leader).sendStatusMessage(tc, true);
        }

        queue.add(queue.remove());
        phase = BattlePhase.WAIT;
        actions = 0;
        actionsStart = 0;

        if (queue.size() < 2) {
//            clear();
            if (leader instanceof EntityPlayer) {
                Combat.MANAGER.destroyBattle((EntityPlayer) leader);
                final ITextComponent tc = new TextComponentString("battle closed");
                tc.getStyle().setColor(TextFormatting.RED);
                ((EntityPlayer) leader).sendStatusMessage(tc, false);
            }
            return;
        }

        final EntityLivingBase newLeader = members.get(getLeader()).get();
        if (newLeader == null) return;
        final ICombatant newCom = CombatantProvider.get(newLeader);
        newCom.setActionPoints(newCom.getActionPoints() + 5);
        CombatantHandler.sync(newLeader);

        if (newLeader instanceof EntityPlayer) {
            final ITextComponent tc = new TextComponentString("your turn");
            tc.getStyle().setColor(TextFormatting.GREEN);
            ((EntityPlayer) newLeader).sendStatusMessage(tc, true);
        }
    }

    private void consumeActionAp(EntityLivingBase entity, ICombatant com) {
        final float apOverhead = com.getActionPointsOverhead();
        final float ap = Rules.attackActionPoints(entity);
        com.setActionPoints(com.getActionPoints() - ap - apOverhead);
        com.setActionPointsOverhead(apOverhead + ap / 2);
    }

    private void consumeMovementAp(EntityLivingBase entity, ICombatant com) {
        final Vec3d pos = entity.getPositionVector();
        final float newAp = com.getActionPoints() - (float) com.getPosition().distanceTo(pos);
        com.setActionPoints(newAp);
        com.setPosition(pos);
    }

    public void addMember(EntityLivingBase entity) {
        if (members.containsKey(entity.getUniqueID())) return;

        members.put(entity.getUniqueID(), new WeakReference<>(entity));
        if (!queue.isEmpty())
            queue.add(entity.getUniqueID());

        final ICombatant com = CombatantProvider.get(entity);
        com.setBattleId(bid);
        com.setActionPoints(0);
        com.setPosition(entity.getPositionVector());
        if (training)
            com.setTrainingHealth(entity.getHealth());
        CombatantHandler.sync(entity);
    }

    public void updateEntity(EntityLivingBase entity) {
        if (!members.containsKey(entity.getUniqueID()))
            members.put(entity.getUniqueID(), new WeakReference<>(entity));
    }

    public void removeMember(UUID entityId) {
        final WeakReference<EntityLivingBase> removedRef = members.remove(entityId);
        final EntityLivingBase removed = removedRef != null ? removedRef.get() : null;
        if (removed != null)
            removeEntity(removed);

        queue.remove(entityId);
        if (isLeader(entityId))
            finishTurn();
    }

    public void clear() {
        members.values().stream()
                .map(Reference::get)
                .filter(Objects::nonNull)
                .forEach(this::removeEntity);

        members.clear();
        queue.clear();
    }

    private void removeEntity(EntityLivingBase entity) {
        if (isTraining())
            restoreHealth(entity);
        final ICombatant com = CombatantProvider.get(entity);
        com.reset();
        CombatantHandler.sync(entity);
    }

    private void restoreHealth(EntityLivingBase entity) {
        final ICombatant com = CombatantProvider.get(entity);
        entity.setHealth(com.getTrainingHealth());
        com.setTrainingHealth(0);
        CombatantHandler.sync(entity);
    }
}
