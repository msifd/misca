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

    public String action_name;
    public boolean described;
    public UUID target;
    public int modifier;

    public float damageDealt;

    public transient EntityLivingBase entity; // Находится по uuid'у
    public transient Action action; // Сериализуется как название действия

    FighterContext(EntityLivingBase entity) {
        this.uuid = EntityUtils.getUuid(entity);
        this.entity = entity;
        reset();
    }

    public void reset() {
        updateStatus(Status.ACT);
        this.control = null;
        this.action = null;
        this.action_name = null;
        this.described = false;
        this.target = null;
        this.modifier = 0;
        this.damageDealt = 0;
    }

    public void updateStatus(Status status) {
        this.status = status;
        this.lastStateChange = System.currentTimeMillis() / 1000;
    }

    public void updateAction(Action action) {
        this.action = action;
        this.action_name = action.name;
        this.described = false;
    }

    public boolean canAttack() {
        return action != null && described && status != Status.WAIT;
    }

    public enum Status {
        ACT, DEAL_DAMAGE, WAIT, KO_ED, LEAVING, REMOVED
    }
}
