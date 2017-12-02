package msifeed.mc.misca.crabs.battle;

import msifeed.mc.misca.crabs.EntityUtils;
import net.minecraft.entity.EntityLivingBase;

import java.io.Serializable;
import java.util.UUID;

public class FighterContext implements Serializable {
    UUID uuid;
    State state;
    long lastStateChange;

    transient EntityLivingBase entity;

    public FighterContext(EntityLivingBase entity) {
        this.uuid = EntityUtils.getUuid(entity);
        this.entity = entity;
        updateState(State.ACTIVE);
    }

    public void updateState(State newState) {
        this.state = newState;
        this.lastStateChange = System.currentTimeMillis() / 1000;
    }

    public boolean canAttack() {
        return state == State.ACTIVE || state == State.DEFEND;
    }

    public boolean isWaitedForLeave() {
        final int LEAVING_WAIT = 5; // in secs
        final long now = System.currentTimeMillis() / 1000;
        return state == FighterContext.State.LEAVING && (now - lastStateChange > LEAVING_WAIT);
    }

    public enum State {
        ACTIVE, ATTACK, DEFEND, KO_ED, DEAD, LEAVING;
    }
}
