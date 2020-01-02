package msifeed.mc.aorta.genesis;

import com.google.gson.JsonObject;

import java.util.HashSet;

public interface Generator {
    void init();

    void generate(JsonObject json, HashSet<GenesisTrait> traits);
}
