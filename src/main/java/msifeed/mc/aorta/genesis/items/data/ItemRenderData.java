package msifeed.mc.aorta.genesis.items.data;

import com.google.gson.JsonObject;
import msifeed.mc.aorta.genesis.JsonUtils;

public class ItemRenderData {
    public float scale = 1;
    public float thickness = 1;
    public float offset = 0;
    public float recess = 0;
    public float rotation = 0;

    public ItemRenderData(JsonObject json) {
        if (!json.has("render") || !json.get("render").isJsonObject())
            return;
        final JsonObject render = json.get("render").getAsJsonObject();
        JsonUtils.consumeFloat(render, "scale", f -> scale = f);
        JsonUtils.consumeFloat(render, "thickness", f -> thickness = f);
        JsonUtils.consumeFloat(render, "offset", f -> offset = f);
        JsonUtils.consumeFloat(render, "recess", f -> recess = f);
        JsonUtils.consumeFloat(render, "rotation", f -> rotation = f);
    }


}
