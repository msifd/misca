package msifeed.mc.aorta.genesis.blocks.data;

import com.google.gson.JsonObject;
import msifeed.mc.aorta.genesis.JsonUtils;

public class TrapData {
    public String closeMessage = "";
    public int closeRadius = 15;
    public String farMessage = "";
    public int farRadius = 60;
    public boolean destroy = false;

    public TrapData(JsonObject json) {
        JsonUtils.consumeString(json, "closeMessage", v -> closeMessage = v);
        JsonUtils.consumeInt(json, "closeRadius", v -> closeRadius = v);
        JsonUtils.consumeString(json, "farMessage", v -> farMessage = v);
        JsonUtils.consumeInt(json, "farRadius", v -> farRadius = v);
        JsonUtils.consumeBool(json, "destroy", v -> destroy = v);
    }
}
