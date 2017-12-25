package msifeed.mc.misca.crabs.context;

import msifeed.mc.misca.crabs.action.Action;
import net.minecraft.entity.EntityLivingBase;

import java.util.UUID;

public class Context {
    public UUID uuid;
    public transient EntityLivingBase entity;

    public Status status;
    public long lastStatusChange;

    public boolean knockedOut;

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
        reset(Status.NEUTRAL);
    }

    public void reset() {
        reset(status.isFighting() ? Status.ACTIVE : Status.NEUTRAL);
    }

    public void reset(Status status) {
        updateStatus(status);

        puppet = null;
        action = null;
        modifier = 0;
        described = false;
        target = null;
        damageDealt = 0;
    }

    public void updateStatus(Status status) {
        this.status = status;
        this.lastStatusChange = System.currentTimeMillis() / 1000;
    }

    public void updateAction(Action action) {
        this.action = action;
        this.described = false;
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
    }
}
