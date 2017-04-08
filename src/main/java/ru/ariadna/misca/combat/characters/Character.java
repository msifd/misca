package ru.ariadna.misca.combat.characters;

public class Character {
    public String name;
    public int strength;
    public int perception;
    public int reflexes;
    public int endurance;
    public int determination;
    public int wisdom;
    public int spirit;

    public int[] toVector() {
        return new int[]{strength, perception, reflexes, endurance, determination, wisdom, spirit};
    }
}
