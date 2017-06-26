package ru.ariadna.misca.crabs.characters;

import java.util.EnumMap;
import java.util.Map;

public class Character {
    public String name;
    public String charsheet;
    public Map<CharStats, Byte> stats = new EnumMap<>(CharStats.class);
}
