package msifeed.mc.misca.crabs.fight;

import net.minecraft.world.World;

import java.util.ArrayList;

public class Fight {
    State state = State.LOBBY;

    String worldName;
    transient World world;

    FightMember leader;
    ArrayList<FightMember> members = new ArrayList<>();

    Fight(World world, FightMember leader) {
        this.worldName = world.getWorldInfo().getWorldName();
        this.world = world;
        this.leader = leader;
    }

    public enum State {
        LOBBY, FIGHT, ENDED
    }
}
