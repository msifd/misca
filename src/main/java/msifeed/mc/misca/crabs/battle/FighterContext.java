package msifeed.mc.misca.crabs.battle;

import net.minecraft.entity.player.EntityPlayer;

import java.io.Serializable;
import java.util.UUID;

public class FighterContext implements Serializable {
    UUID uuid;
    State state;
    long lastStateChange;

    transient EntityPlayer player;

    public FighterContext(EntityPlayer player) {
        this.uuid = player.getUniqueID();
        this.player = player;

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
