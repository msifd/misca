package msifeed.mc.misca.crabs.actions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class ActionJsonDeserializer implements JsonDeserializer<Action> {
    @Override
    public Action deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        RawAction raw = context.deserialize(json, RawAction.class);

        Action act = new Action(raw.name, Action.Type.valueOf(raw.type.toUpperCase()));
        act.modifiers.addAll(raw.rolls.stream().map(ActionParser::parseRoll).collect(Collectors.toList()));
        act.target_effects.addAll(raw.target_effects.stream().map(ActionParser::parseEffect).collect(Collectors.toList()));
        act.self_effects.addAll(raw.self_effects.stream().map(ActionParser::parseEffect).collect(Collectors.toList()));
        act.tags.addAll(raw.tags);

        return act;
    }

    private static class RawAction {
        String name = "?";
        String type;
        LinkedList<String> rolls = new LinkedList<>();
        LinkedList<String> target_effects = new LinkedList<>();
        LinkedList<String> self_effects = new LinkedList<>();
        LinkedList<String> tags = new LinkedList<>();
    }
}
