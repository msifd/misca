package msifeed.mc.misca.crabs.character;

import java.util.EnumMap;

public class Character {
    public String name = "";
    public EnumMap<Stats, Integer> stats = new EnumMap<>(Stats.class);

    public Character() {
    }

    public Character(int[] stats) {
        final Stats[] sv = Stats.values();
        for (int i = 0; i < sv.length; i++) {
            this.stats.put(sv[i], stats[i]);
        }
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
