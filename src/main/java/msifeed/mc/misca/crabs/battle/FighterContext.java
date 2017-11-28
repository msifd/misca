package msifeed.mc.misca.crabs.battle;

import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class FighterContext {
    UUID uuid;
    EntityPlayer player;
    State state = State.ACTIVE;

    public FighterContext(EntityPlayer player) {
        this.uuid = player.getUniqueID();
        this.player = player;
    }

    public enum State {
        ACTIVE, LEAVING, KO_ED
    }
}
