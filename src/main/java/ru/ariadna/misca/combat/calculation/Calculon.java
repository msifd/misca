package ru.ariadna.misca.combat.calculation;

import ru.ariadna.misca.combat.characters.Character;
import ru.ariadna.misca.combat.fight.Action;

import java.util.Random;

public class Calculon {
    private static Random rnd = new Random();
    private final CalcRulesProvider provider;

    public Calculon(CalcRulesProvider provider) {
        this.provider = provider;
    }

    public static CalcResult calculate(CalcRule rule, Character character) {
        CalcResult res = new CalcResult();

        for (int edges : rule.dices) {
            int roll = dice(edges);
            res.result += roll;
            res.rolls.add(roll);
        }

        int[] stats = character.toVector();
        for (int i = 0; i < stats.length; i++) {
            float coeff = rule.coefficients[i];
            if (coeff != 0) {
                res.result += Math.floor(stats[i] * coeff);
                res.stats.add(stats[i]);
                res.coeffs.add(coeff);
            }
        }

        res.result += rule.mod;
        res.mods = rule.mod;

        return res;
    }

    private static int dice(int d) {
        return rnd.nextInt(d) + 1;
    }

    public void init() {
        rnd.setSeed(System.currentTimeMillis());
    }

    public CalcResult calculate(Character character, Action action, int mod) {
        CalcResult result = calculate(provider.getRule(action), character);
        result.mods += mod;
        return result;
    }
}
