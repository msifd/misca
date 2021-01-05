package msifeed.sys.sync;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.ZoneId;

public class ZoneIdAdapter implements JsonSerializer<ZoneId>, JsonDeserializer<ZoneId> {
    @Override
    public JsonElement serialize(ZoneId src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

    @Override
    public ZoneId deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return ZoneId.of(json.getAsJsonPrimitive().getAsString());
    }
}