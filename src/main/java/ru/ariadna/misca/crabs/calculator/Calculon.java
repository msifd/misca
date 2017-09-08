package ru.ariadna.misca.crabs.calculator;

import ru.ariadna.misca.crabs.characters.CharStats;
import ru.ariadna.misca.crabs.characters.Character;
import ru.ariadna.misca.crabs.combat.parts.ActionType;

import java.util.*;
import java.util.function.Function;

public class Calculon {
    private static final Random rand = new Random();
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
        result.dice = roll_fight();
        result.stats = rules.get(actionType).apply(character);
        result.mod = mod;
        result.result = result.dice + result.stats + result.mod;
        return result;
    }

    public static int roll_fight() {
        return (int) Math.floor(gaussRoll(4.5, 1, 21));
    }

    public static int roll_stat() {
        return (int) Math.round(gaussRoll(4.5, 1, 21) / 2.);
    }

    private static int fate(int times) {
        return rand.ints(times, -1, 2).sum();
    }

    private static int posFate(int times, int n) {
        final int offset = times + 1;
        final float scale = (float) n / (times + times + 1f);
        return (int) Math.ceil((fate(times) + offset) * scale);
    }

    private static int dice(int n) {
        return rand.nextInt(n) + 1;
    }

    private static double gaussRoll(double mean, int min, int max) {
        final double std_dev = min / 2. + max / 2.;
        double roll = min - 1;
        while (roll < min || roll > max) {
            roll = rand.nextGaussian() * mean + std_dev;
        }
        return roll;
    }

//    public static void main(String[] args) {
//        int total = 1000000;
//        System.out.println("Rule: ~(gaussDice(4.5, 1, 21) / 2)");
//        java.util.stream.IntStream.range(0, total)
//            .map(i -> (int) Math.floor(gaussRoll(4.5, 1, 21))) // fight roll
////            .map(i -> (int) Math.round(gaussRoll(4.5, 1, 21) / 2.)) // stat roll
//            .boxed()
//            .collect(Collectors.groupingBy(Function.identity(), TreeMap::new, Collectors.counting()))
//            .forEach((i, c) -> {
//                float percent = c / (float) total * 100;
//                int bars_n = (int) Math.round(percent);
//                String bars = String.join("", Collections.nCopies(bars_n, "#"));
//                System.out.println(String.format("%2d: %4.1f%% %s", i, percent, bars));
//            });
//    }
}
