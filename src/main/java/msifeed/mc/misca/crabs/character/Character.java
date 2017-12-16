package msifeed.mc.misca.crabs.character;

import java.util.EnumMap;

public class Character {
    public String name = "";
    public boolean isPlayer = false; // Используется только при сохранении стат, отсеивая не-игроков
    public EnumMap<Stats, Integer> stats = new EnumMap<>(Stats.class);

    Character() {
    }

    public Character(int[] stats) {
        fill(stats);
    }

    public void fill(int[] stats) {
        final Stats[] sv = Stats.values();
        for (int i = 0; i < sv.length; i++) {
            this.stats.put(sv[i], stats[i]);
        }
    }

    public int stat(Stats s) {
        return stats.getOrDefault(s, 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Character)) return false;
        final Character character = (Character) obj;
        return this.stats.equals(character.stats);
    }
}
