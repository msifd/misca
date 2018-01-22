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
            if (el.isJsonPrimitive() && el.getAsJsonPrimitive().isString()) {
                final ControlEntry c = new ControlEntry();
                c.aClass = lookupClass(el.getAsString());
                result.add(c);
            } else if (el.isJsonObject()) {
                final JsonObject entry = el.getAsJsonObject();

                final JsonElement nameElement = entry.get("class");
                if (nameElement == null || !nameElement.isJsonPrimitive() || !nameElement.getAsJsonPrimitive().isString())
                    throw new JsonParseException("Entry must contain `name` string!");

                final ControlEntry c = new ControlEntry();
                c.aClass = lookupClass(nameElement.getAsString());

                final JsonElement dimsElement = entry.get("dims");
                if (dimsElement != null && dimsElement.isJsonArray()) {
                    c.dimensions = new HashSet<>();
                    for (JsonElement de : dimsElement.getAsJsonArray()) {
                        if (de == null || !de.isJsonPrimitive() || !de.getAsJsonPrimitive().isString())
                            throw new JsonParseException("`dim` entry must be a string!");
                        c.dimensions.add(de.getAsString());
                    }
                }

                result.add(c);
            } else {
                throw new JsonParseException("Entry must by string or object!");
            }
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
