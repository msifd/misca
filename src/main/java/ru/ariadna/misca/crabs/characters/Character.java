package ru.ariadna.misca.crabs.characters;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

public class Character implements Serializable {
    public String name;
    public Map<CharStats, Byte> stats = new EnumMap<>(CharStats.class);
    public String charsheet;
}
