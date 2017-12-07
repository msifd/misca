package msifeed.mc.misca.crabs.character;

import java.io.Serializable;
import java.util.EnumMap;

public class Character implements Serializable {
    public EnumMap<Stats, Integer> stats = new EnumMap<>(Stats.class);

    public void fill(int... values) {
        final Stats[] statTypes = Stats.values();
        if (values.length != statTypes.length) throw new RuntimeException("Stats fill length mismatch");
        for (int i = 0; i < statTypes.length; i++) stats.put(statTypes[i], values[i]);
    }

    public int stat(Stats s) {
        return stats.getOrDefault(s, 0);
    }
}
