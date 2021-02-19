package msifeed.misca.locks;

import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class LocksConfig {
    public Map<ResourceLocation, Lookup> tileless = new HashMap<>();
    public int minPins = 3;
    public int maxPins = 9;
    public int pinPositions = 9;
    public int setupKeysCount = 2;

    public LocksConfig() {
        tileless.put(new ResourceLocation("minecraft", "trapdoor"), Lookup.single);
        tileless.put(new ResourceLocation("minecraft", "wooden_door"), Lookup.door);
        tileless.put(new ResourceLocation("minecraft", "chest"), Lookup.adjacent);
        tileless.put(new ResourceLocation("minecraft", "trapped_chest"), Lookup.adjacent);
    }

    public enum Lookup {
        single, door, adjacent
    }
}
