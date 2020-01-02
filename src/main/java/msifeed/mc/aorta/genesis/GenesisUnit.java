package msifeed.mc.aorta.genesis;

import com.google.gson.JsonObject;

import java.util.HashSet;

public class GenesisUnit {
    public String id;
    public HashSet<GenesisTrait> traits;

    public GenesisUnit(JsonObject json, HashSet<GenesisTrait> traits) {
        this.id = json.getAsJsonPrimitive("id").getAsString();
        this.traits = traits;
    }

    public boolean hasTrait(GenesisTrait trait) {
        return traits.contains(trait);
    }
}
