package msifeed.mc.misca.crabs.character;

import java.io.Serializable;
import java.util.EnumMap;

public class Character implements Serializable {
    public EnumMap<Stats, Integer> stats = new EnumMap<>(Stats.class);

    public int stat(Stats s) {
        return stats.getOrDefault(s, 0);
    }
}
