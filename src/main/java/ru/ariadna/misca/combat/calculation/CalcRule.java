package ru.ariadna.misca.combat.calculation;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalcRule {
    private static final Pattern rule_pattern = Pattern.compile("(d\\d{1,2})|(\\w{3}\\*[.\\d]{1,3})|(-?\\d{1,2})");

    int[] dices;
    float[] coefficients = new float[7];
    int mod = 0;

    public CalcRule(String raw) throws RuleParseException {
        Matcher match = rule_pattern.matcher(raw);
        boolean found = false;

        LinkedList<String> dices = new LinkedList<>();
        LinkedList<String> coefficients = new LinkedList<>();
        LinkedList<String> mods = new LinkedList<>();
        while (match.find()) {
            String d = match.group(1);
            String c = match.group(2);
            String m = match.group(3);
            if (d != null) dices.add(d);
            else if (c != null) coefficients.add(c);
            else if (m != null) mods.add(m);
            else {
                throw new RuleParseException(RuleParseException.Type.FORMAT);
            }
            found = true;
        }

        if (!found) {
            throw new RuleParseException(RuleParseException.Type.FORMAT);
        }

        this.dices = new int[dices.size()];
        for (int i = 0; i < dices.size(); i++) {
            try {
                this.dices[i] = Integer.valueOf(dices.get(i).substring(1));
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
                this.coefficients[index] = number;
            } catch (NumberFormatException e) {
                throw new RuleParseException(RuleParseException.Type.COEFF);
            }
        }

        for (String m : mods) {
            try {
                this.mod += Integer.valueOf(m);
            } catch (NumberFormatException e) {
                throw new RuleParseException(RuleParseException.Type.MOD);
            }
        }
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

    @Override
    public String toString() {
        return "rolls: " + Arrays.toString(dices) + " coeffs: " + Arrays.toString(coefficients) + " mod: " + mod;
    }

    public static class RuleParseException extends Exception {
        final Type type;

        RuleParseException(Type t) {
            type = t;
        }

        public enum Type {
            FORMAT, DICE, STAT, COEFF, MOD
        }
    }
}
