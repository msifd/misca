package msifeed.mc.misca.crabs.context;

import msifeed.mc.misca.crabs.action.Action;
import msifeed.mc.misca.crabs.rules.Buff;
import net.minecraft.entity.EntityLivingBase;

import java.util.LinkedList;
import java.util.UUID;

public class Context {
    public UUID uuid;
    public transient EntityLivingBase entity;

    public Status status;
    public long lastStatusChange;

    public boolean knockedOut;
    public transient LinkedList<Buff> buffs = new LinkedList<>();
    public transient LinkedList<String> buffNames = new LinkedList<>(); // Для вывода клиентом. Сами баффы живут на сервере.

    // Штуки для сражений
    public UUID puppet;
    public transient Action action;
    public int modifier;
    public boolean described;
    public UUID target;
    public float damageDealt;

    Context(UUID uuid, EntityLivingBase entity) {
        this.uuid = uuid;
        this.entity = entity;
        softReset(Status.NEUTRAL);
    }

    public void endEffects() {
        buffs.removeIf(Buff::ended);
    }

    public void softReset() {
        softReset(status.isFighting() ? Status.ACTIVE : Status.NEUTRAL);
    }

    public void softReset(Status status) {
        updateStatus(status);

        puppet = null;
        action = null;
        modifier = 0;
        described = false;
        target = null;
        damageDealt = 0;
    }

    public void hardReset() {
        softReset(Status.NEUTRAL);

        knockedOut = false;
        buffs.clear();
        buffNames.clear();
    }

    public void updateStatus(Status status) {
        this.status = status;
        this.lastStatusChange = System.currentTimeMillis() / 1000;
    }

    public void updateAction(Action action) {
        this.action = action;
        this.described = false;
        this.damageDealt = 0;
    }

    public boolean canSelectAction() {
        return status == Status.ACTIVE && !knockedOut;
    }

    public boolean canAttack() {
        return action != null && described && !knockedOut && (status.ordinal() <= Status.DEAL_DAMAGE.ordinal());
    }

    public enum Status {
        ACTIVE, DEAL_DAMAGE, WAIT, LEAVING, NEUTRAL, DELETE;

        public boolean isFighting() {
            return ordinal() <= LEAVING.ordinal();
        }

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}
