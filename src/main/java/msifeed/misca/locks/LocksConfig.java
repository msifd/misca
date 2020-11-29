package msifeed.misca.locks;

import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class LocksConfig {
    public Map<ResourceLocation, Lookup> tileless = new HashMap<>();

    public LocksConfig() {
        tileless.put(new ResourceLocation("minecraft", "trapdoor"), Lookup.single);
        tileless.put(new ResourceLocation("minecraft", "wooden_door"), Lookup.door);
    }

    public enum Lookup {
        single, door
    }
}
