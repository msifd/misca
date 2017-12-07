package msifeed.mc.misca.crabs.actions;

import msifeed.mc.misca.crabs.character.Stats;
import msifeed.mc.misca.crabs.rules.Effect;
import msifeed.mc.misca.crabs.rules.Modifier;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActionParser {
    public static Action parse(String line) throws ParseException {
        String[] parts = line.replaceAll("\\s", "").split(":");
        if (parts.length != 6) throw new ParseException("Liner action missing blocks!");

        Action act = new Action(parts[0], Action.Type.valueOf(parts[1].toUpperCase()));
        act.modifiers.addAll(Stream.of(parts[2].split(",")).map(ActionParser::parseRoll).collect(Collectors.toList()));
        act.modifiers.addAll(Stream.of(parts[3].split(",")).map(ActionParser::parseRoll).collect(Collectors.toList()));
        act.modifiers.addAll(Stream.of(parts[4].split(",")).map(ActionParser::parseRoll).collect(Collectors.toList()));
        act.tags.addAll(Arrays.asList(parts[5].split(",")));

        return act;
    }

    public static Modifier parseRoll(String str) {
        switch (str.toLowerCase()) {
            case "g30":
                return new Modifier.DiceG30();
            case "g30+":
                return new Modifier.DiceG30Plus();
            case "g30-":
                return new Modifier.DiceG30Minus();

            case "str":
            case "ref":
            case "per":
            case "det":
            case "int":
            case "mag":
                return new Modifier.Stat(Stats.valueOf(str.toUpperCase()));

            default:
                try {
                    return new Modifier.Const(Integer.parseInt(str));
                } catch (NumberFormatException e) {
                    throw new ParseException("Unknown Roll: '" + str + "'!");
                }
        }
    }

    public static Effect parseEffect(String str) {
        switch (str) {
            case "damage":
                return new Effect.Damage();
            default:
                throw new ParseException("Unknown Effect: '" + str + "'!");
        }
    }

    public static class ParseException extends RuntimeException {
        public ParseException(String cause) {
            super(cause);
        }
    }
}
