package ru.ariadna.misca.combat;

import com.google.common.base.Joiner;
import ru.ariadna.misca.combat.characters.Character;
import ru.ariadna.misca.combat.fight.Action;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculon {
    private static final Pattern rule_pattern = Pattern.compile("(d\\d{1,2})|(\\w{3}\\*[.\\d]{1,3})");

    private Random rnd = new Random();
    private EnumMap<Action, CalcRule> rules = new EnumMap<>(Action.class);

    private static CalcRule parseRule(String rule_str) throws RuleParseException {
        Matcher m = rule_pattern.matcher(rule_str);
        boolean found = false;

        LinkedList<String> dices = new LinkedList<>();
        LinkedList<String> coefficients = new LinkedList<>();
        while (m.find()) {
            String d = m.group(1);
            String c = m.group(2);
            if (d != null) dices.add(d);
            else if (c != null) coefficients.add(c);
            else {
                throw new RuleParseException(RuleParseException.Type.FORMAT);
            }
            found = true;
        }

        if (!found) {
            throw new RuleParseException(RuleParseException.Type.FORMAT);
        }

        CalcRule rule = new CalcRule();

        rule.dices = new int[dices.size()];
        for (int i = 0; i < dices.size(); i++) {
            try {
                rule.dices[i] = Integer.valueOf(dices.get(i).substring(1));
            } catch (NumberFormatException e) {
                throw new RuleParseException(RuleParseException.Type.DICE);
            }
        }

        for (String c : coefficients) {
            String stat = c.substring(0, 3);
            String num_str = c.substring(4);

            int index = statIndex(stat);
            if (index < 0) {
                throw new RuleParseException(RuleParseException.Type.STAT);
            }

            try {
                float number = Float.valueOf(num_str);
                rule.coefficients[index] = number;
            } catch (NumberFormatException e) {
                throw new RuleParseException(RuleParseException.Type.COEFF);
            }
        }

        return rule;
    }

    public static void main(String[] a) throws RuleParseException {
//        String rule_string = "d10 d20 str*0.5 per*0.5 ref*0.5 det*0.5";
//        CalcRule rule = parseRule(rule_string);
//        System.out.println(rule.toString());

//        CalcResult cr = new CalcResult();
//        cr.result = 228;
//        cr.rolls = Arrays.asList(13);
//        cr.stats = Arrays.asList(8, 4);
//        cr.coeffs = Arrays.asList(0.5f, 0.5f);
//        System.out.println(cr.toString());
    }

    private static int statIndex(String s) {
        switch (s) {
            case "str":
                return 0;
            case "per":
                return 1;
            case "ref":
                return 2;
            case "end":
                return 3;
            case "det":
                return 4;
            case "wis":
                return 5;
            case "spr":
                return 6;
            default:
                return -1;
        }
    }

    void load() {
        rnd.setSeed(System.currentTimeMillis());
    }

    CalcResult calculate(Character character, Action action, int mod) {
        CalcRule rule = rules.get(action);

        CalcResult res = new CalcResult();
        res.result = mod;

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

        return res;
    }

    private int dice(int d) {
        return rnd.nextInt(d) + 1;
    }

    public static class CalcResult {
        int result;
        List<Integer> rolls = new LinkedList<>();
        List<Integer> stats = new LinkedList<>();
        List<Float> coeffs = new LinkedList<>();

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append(Joiner.on(" + ").join(rolls));

            Iterator<Integer> s_iter = stats.listIterator();
            Iterator<Float> c_iter = coeffs.listIterator();
            while (s_iter.hasNext()) {
                sb.append(" + ");
                sb.append(s_iter.next());
                sb.append("*");
                sb.append(c_iter.next());
            }
            sb.append(" = ");
            sb.append(result);

            return sb.toString();
        }
    }

    private static class CalcRule {
        int[] dices;
        float[] coefficients = new float[7];

        @Override
        public String toString() {
            return "rolls: " + Arrays.toString(dices) + " coeffs: " + Arrays.toString(coefficients);
        }
    }

    public static class RuleParseException extends Exception {
        final Type type;

        RuleParseException(Type t) {
            type = t;
        }

        public enum Type {
            FORMAT, DICE, STAT, COEFF
        }
    }
}
