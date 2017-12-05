package msifeed.mc.misca.crabs.battle;

import msifeed.mc.misca.crabs.EntityUtils;
import msifeed.mc.misca.crabs.actions.Action;
import net.minecraft.entity.EntityLivingBase;

import java.io.Serializable;
import java.util.UUID;

public class FighterContext implements Serializable {
    public UUID uuid;
    public Status status;
    public long lastStateChange;
    public UUID control = null;

    public Stage stage;
    public Action action;
    public boolean described;
    public UUID target;

    public transient EntityLivingBase entity;

    public FighterContext(EntityLivingBase entity) {
        this.uuid = EntityUtils.getUuid(entity);
        this.entity = entity;
        updateStatus(Status.IDLE);
        resetMove();
    }

    public void resetMove() {
        this.stage = Stage.ACT;
        this.action = null;
        this.described = false;
        this.target = null;
    }

    public void updateStatus(Status status) {
        this.status = status;
        this.lastStateChange = System.currentTimeMillis() / 1000;

        resetMove();
    }

    public void updateAction(Action action) {
        this.action = action;
        this.described = false;
    }

    public boolean canAttack() {
        return action != null && described && stage != Stage.WAIT;
    }

    public boolean canLeaveNow() {
        final long now = System.currentTimeMillis() / 1000;
        return status == Status.LEAVING && (now - lastStateChange > BattleDefines.SECS_BEFORE_LEAVE_BATTLE);
    }

    public enum Status {
        IDLE, ACTIVE, KO_ED, MISSING, LEAVING;
    }

    public enum Stage {
        ACT, DEAL_DAMAGE, WAIT
    }
}
