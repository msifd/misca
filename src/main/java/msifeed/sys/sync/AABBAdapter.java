package msifeed.sys.sync;

import com.google.gson.*;
import net.minecraft.util.math.AxisAlignedBB;

import java.lang.reflect.Type;

public class AABBAdapter implements JsonDeserializer<AxisAlignedBB>, JsonSerializer<AxisAlignedBB> {
    @Override
    public AxisAlignedBB deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonArray()) throw new JsonParseException("AxisAlignedBB should be an array");
        final JsonArray array = json.getAsJsonArray();
        if (array.size() != 6) throw new JsonParseException("AxisAlignedBB array size is not equals 6");
        for (int i = 0; i < 6; i++) {
            final JsonElement el = array.get(i);
            if (!el.isJsonPrimitive() || !el.getAsJsonPrimitive().isNumber()) throw new JsonParseException("AxisAlignedBB array member is not a number");
        }

        return new AxisAlignedBB(
                array.get(0).getAsDouble(),
                array.get(1).getAsDouble(),
                array.get(2).getAsDouble(),
                array.get(3).getAsDouble(),
                array.get(4).getAsDouble(),
                array.get(5).getAsDouble()
        );
    }

    @Override
    public JsonElement serialize(AxisAlignedBB src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonArray array = new JsonArray();
        array.add(new JsonPrimitive((long) src.minX));
        array.add(new JsonPrimitive((long) src.minY));
        array.add(new JsonPrimitive((long) src.minZ));
        array.add(new JsonPrimitive((long) src.maxX));
        array.add(new JsonPrimitive((long) src.maxY));
        array.add(new JsonPrimitive((long) src.maxZ));
        return array;
    }
}
