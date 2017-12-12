package msifeed.mc.misca.crabs.character;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Character implements Serializable {
    public EnumMap<Stats, Integer> stats = new EnumMap<>(Stats.class);

    public Character() {
    }

    public Character(int s, int r, int p, int d, int i, int m) {
        stats.put(Stats.STR, s);
        stats.put(Stats.REF, r);
        stats.put(Stats.PER, p);
        stats.put(Stats.DET, d);
        stats.put(Stats.INT, i);
        stats.put(Stats.MAG, m);
    }

    public int stat(Stats s) {
        return stats.getOrDefault(s, 0);
    }
}
