package msifeed.misca.locks;

import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class LocksConfig {
    public Map<ResourceLocation, Lookup> tileless = new HashMap<>();

    public int setupKeysCount = 2;
    public int defaultPinPositions = 3;
    public double pinPickChanceBase = 0.5;
    public double pinPickChancePosMod = 0.01;
    public double pickBreakChance = 0.05;

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
