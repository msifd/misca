package msifeed.mc.misca.crabs.actions;

import msifeed.mc.misca.crabs.rules.Effect;
import msifeed.mc.misca.crabs.rules.Modifier;
import msifeed.mc.misca.crabs.rules.Rules;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActionParser {
    public static Action parse(String line) throws ParseException {
        String[] parts = line.replaceAll("\\s", "").split(":");
        if (parts.length != 6) throw new ParseException("Liner action missing blocks!");

        Action act = new Action(parts[0], parts[1], Action.Type.valueOf(parts[1].toUpperCase()));
        act.modifiers.addAll(Stream.of(parts[2].split(",")).map(ActionParser::parseMod).collect(Collectors.toList()));
        act.modifiers.addAll(Stream.of(parts[3].split(",")).map(ActionParser::parseMod).collect(Collectors.toList()));
        act.modifiers.addAll(Stream.of(parts[4].split(",")).map(ActionParser::parseMod).collect(Collectors.toList()));
        act.tags.addAll(Arrays.asList(parts[5].split(",")));

        return act;
    }

    public static Modifier parseMod(String str) {
        final Modifier constMod = Rules.mod(str);
        if (constMod != null) return constMod;
        throw new ParseException("Unknown Modifier: '" + str + "'!");
    }

    public static Effect parseEffect(String str) {
        final Effect constEffect = Rules.effect(str);
        if (constEffect != null) return constEffect;
        throw new ParseException("Unknown Effect: '" + str + "'!");
    }

    public static class ParseException extends RuntimeException {
        public ParseException(String cause) {
            super(cause);
        }
    }
}
