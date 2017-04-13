package ru.ariadna.misca.combat.characters;

public class Character {
    public String name = "";
    public int strength = 5;
    public int perception = 5;
    public int reflexes = 5;
    public int endurance = 5;
    public int determination = 5;
    public int wisdom = 5;
    public int spirit = 5;

    public int[] toVector() {
        return new int[]{strength, perception, reflexes, endurance, determination, wisdom, spirit};
    }

    public String stats() {
        return String.format("str:%d per:%d ref:%d end:%d det:%d wis:%d spr:%d",
                strength, perception, reflexes, endurance, determination, wisdom, spirit);
    }
}
