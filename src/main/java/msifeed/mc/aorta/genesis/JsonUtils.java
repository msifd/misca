package msifeed.mc.aorta.genesis;

import com.google.gson.JsonObject;

import java.util.function.Consumer;

public class JsonUtils {
    public static void consumeString(JsonObject json, String name, Consumer<String> consumer) {
        if (!json.has(name) || !json.get(name).isJsonPrimitive() || !json.get(name).getAsJsonPrimitive().isString())
            return;
        consumer.accept(json.get(name).getAsJsonPrimitive().getAsString());
    }

    public static void consumeFloat(JsonObject json, String name, Consumer<Float> consumer) {
        if (!json.has(name) || !json.get(name).isJsonPrimitive() || !json.get(name).getAsJsonPrimitive().isNumber())
            return;
        consumer.accept(json.get(name).getAsJsonPrimitive().getAsFloat());
    }

    public static void consumeInt(JsonObject json, String name, Consumer<Integer> consumer) {
        if (!json.has(name) || !json.get(name).isJsonPrimitive() || !json.get(name).getAsJsonPrimitive().isNumber())
            return;
        consumer.accept(json.get(name).getAsJsonPrimitive().getAsInt());
    }

    public static void consumeBool(JsonObject json, String name, Consumer<Boolean> consumer) {
        if (!json.has(name) || !json.get(name).isJsonPrimitive() || !json.get(name).getAsJsonPrimitive().isBoolean())
            return;
        consumer.accept(json.get(name).getAsJsonPrimitive().getAsBoolean());
    }
}
