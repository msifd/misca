package ru.ariadna.misca.crabs.calculator;

import ru.ariadna.misca.crabs.characters.Character;
import ru.ariadna.misca.crabs.combat.parts.ActionType;

public class CalcResult {
    public Character character;
    public ActionType action;
    public int dice;
    public int stats;
    public int mod;
    public int result;

    CalcResult(Character character) {
        this.character = character;
    }
}
