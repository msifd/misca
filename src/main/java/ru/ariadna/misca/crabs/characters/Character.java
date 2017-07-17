package ru.ariadna.misca.crabs.characters;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

public class Character implements Serializable {
    public String name;
    public Map<CharStats, Byte> stats = new EnumMap<>(CharStats.class);
    public String charsheet;

    public static Character makeDummy() {
        Character c = new Character();
        for (CharStats cs : CharStats.values()) c.stats.put(cs, (byte) 5);
        return c;
    }

    public int get(CharStats cs) {
        return stats.get(cs);
    }
}
