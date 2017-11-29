package msifeed.mc.misca.crabs.battle;

import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.Sys;

import java.util.UUID;

public class FighterContext {
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

    public enum State {
        ACTIVE, LEAVING, KO_ED, DEAD
    }
}
