package msifeed.sys.sync;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ClassAdapter implements JsonDeserializer<Class<?>>, JsonSerializer<Class<?>> {
    @Override
    public Class<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonPrimitive() || json.getAsJsonPrimitive().isString()) throw new JsonParseException("Class name should be string");

        try {
            return Class.forName(json.getAsString());
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Unknown class: " + json.getAsString());
        }
    }

    @Override
    public JsonElement serialize(Class<?> src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getName());
    }
}
