package ru.ariadna.misca.crabs.calculator;

import ru.ariadna.misca.crabs.characters.CharStats;
import ru.ariadna.misca.crabs.characters.Character;
import ru.ariadna.misca.crabs.combat.parts.ActionType;

import java.util.EnumMap;
import java.util.Random;
import java.util.function.Function;

public class Calculon {
    private static EnumMap<ActionType, Function<Character, Integer>> rules = new EnumMap<>(ActionType.class);

    static {
        rules.put(ActionType.PHYSICAL, c -> c.get(CharStats.STRENGTH) * 2 + c.get(CharStats.DETERMINATION) + c.get(CharStats.REFLEXES));
        rules.put(ActionType.SHOOT, c -> c.get(CharStats.PERCEPTION) * 2 + c.get(CharStats.DETERMINATION) + c.get(CharStats.INTELLIGENCE));
        rules.put(ActionType.DEFEND, c -> c.get(CharStats.REFLEXES) * 2 + c.get(CharStats.DETERMINATION) + c.get(CharStats.STRENGTH));
        rules.put(ActionType.MAGIC, c -> c.get(CharStats.SPIRIT) * 2 + c.get(CharStats.PERCEPTION) + c.get(CharStats.INTELLIGENCE));
    }

    public static CalcResult calc_d20(Character character, ActionType actionType, int mod) {
        CalcResult result = new CalcResult(character);
        result.dice = (int) Math.floor(rollFate(3) + 4 * (20f / 7f));
        result.stats = rules.get(actionType).apply(character);
        result.mod = mod;
        result.result = result.dice + result.stats + result.mod;
        return result;
    }

    public static int roll_d10() {
        return (int) Math.floor(rollFate(3) + 4 * (10f / 7f));
    }

    private static int rollFate(int times) {
        Random random = new Random();
        return random.ints(times, -1, 2).sum();
    }
}
