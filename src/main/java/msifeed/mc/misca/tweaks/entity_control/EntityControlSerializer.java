package msifeed.mc.misca.tweaks.entity_control;

import com.google.gson.*;
import msifeed.mc.misca.tweaks.entity_control.EntityControl.ControlEntry;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class EntityControlSerializer implements JsonDeserializer<List<ControlEntry>> {

    @Override
    public List<ControlEntry> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonArray()) throw new JsonParseException("Root element must be an array!");
        final JsonArray root = json.getAsJsonArray();
        final ArrayList<ControlEntry> result = new ArrayList<>();

        for (JsonElement el : root) {
            final ControlEntry c = new ControlEntry();
            if (el.isJsonPrimitive() && el.getAsJsonPrimitive().isString()) {
                c.classes.add(lookupClass(el.getAsString()));
            } else if (el.isJsonObject()) {
                final JsonObject entry = el.getAsJsonObject();

                final JsonElement classesElement = entry.get("classes");
                if (classesElement == null || !classesElement.isJsonArray())
                    throw new JsonParseException("Entry must contain `classes` string array!");
                for (JsonElement ce : classesElement.getAsJsonArray()) {
                    if (ce == null || !ce.isJsonPrimitive() || !ce.getAsJsonPrimitive().isString())
                        throw new JsonParseException("`classes` entry must be a string!");
                    c.classes.add(lookupClass(ce.getAsString()));
                }

                final JsonElement dimsElement = entry.get("dims");
                if (dimsElement != null && dimsElement.isJsonArray()) {
                    c.dimensions = new HashSet<>();
                    for (JsonElement de : dimsElement.getAsJsonArray()) {
                        if (de == null || !de.isJsonPrimitive() || !de.getAsJsonPrimitive().isString())
                            throw new JsonParseException("`dim` entry must be a string!");
                        c.dimensions.add(de.getAsString());
                    }
                }
            } else {
                throw new JsonParseException("Entry must by string or object!");
            }
            result.add(c);
        }

        return result;
    }

    private Class lookupClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Unknown class: " + name);
        }
    }
}
