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
        rules.put(ActionType.HIT, c -> c.get(CharStats.STRENGTH) * 2 + c.get(CharStats.DETERMINATION) + c.get(CharStats.REFLEXES));
        rules.put(ActionType.SHOOT, c -> c.get(CharStats.PERCEPTION) * 2 + c.get(CharStats.DETERMINATION) + c.get(CharStats.INTELLIGENCE));
        rules.put(ActionType.DEFENCE, c -> c.get(CharStats.REFLEXES) * 2 + c.get(CharStats.DETERMINATION) + c.get(CharStats.STRENGTH));
        rules.put(ActionType.MAGIC, c -> c.get(CharStats.SPIRIT) * 2 + c.get(CharStats.PERCEPTION) + c.get(CharStats.INTELLIGENCE));
    }

    public static CalcResult calc_fight(Character character, ActionType actionType, int mod) {
        CalcResult result = new CalcResult(character);
        result.action = actionType;
        result.dice = posFate(2, 10) - 1 + dice(11);
        result.stats = rules.get(actionType).apply(character);
        result.mod = mod;
        result.result = result.dice + result.stats + result.mod;
        return result;
    }

    public static int roll_stat() {
        return posFate(2, 5) - 1 + dice(6);
    }

    private static int fate(int times) {
        return new Random().ints(times, -1, 2).sum();
    }

    private static int posFate(int times, int n) {
        final int offset = times + 1;
        final float scale = (float) n / (times + times + 1f);
        return (int) Math.ceil((fate(times) + offset) * scale);
    }

    private static int dice(int n) {
        return new Random().nextInt(n) + 1;
    }

//    public static void main(String[] args) {
//        IntStream.range(0, 50).forEach(i -> {
////            System.out.print(posFate(2, 3) + " ");
////            System.out.print(posFate(3, 15) + dice(15) + " ");
////            System.out.print(posFate(3, 10) + dice(10) + " ");
//
////            System.out.print(posFate(2, 5) - 1 + dice(6) + " ");
//            System.out.print(posFate(2, 10) - 1 + dice(11) + " ");
//        });
//    }
}
