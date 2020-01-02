package msifeed.mc.aorta.genesis.blocks;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import msifeed.mc.aorta.genesis.Genesis;
import msifeed.mc.aorta.genesis.GenesisTrait;
import msifeed.mc.aorta.genesis.GenesisUnit;
import msifeed.mc.aorta.genesis.JsonUtils;
import msifeed.mc.aorta.genesis.blocks.data.TrapData;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class BlockGenesisUnit extends GenesisUnit {
    public String textureString = null;
    public String title = null;
    public String[] description;
    public TrapData trapData = null;
    List<String> textureArray = null;
    int[] textureLayout = null;

    BlockGenesisUnit(JsonObject json, HashSet<GenesisTrait> traits) {
        super(json, traits);

        if (json.has(Props.texture)) {
            final JsonElement tEl = json.get(Props.texture);
            if (tEl.isJsonPrimitive() && tEl.getAsJsonPrimitive().isString()) {
                textureString = tEl.getAsJsonPrimitive().getAsString();
            } else if (tEl.isJsonArray()) {
                textureArray = StreamSupport.stream(tEl.getAsJsonArray().spliterator(), false)
                        .limit(6)
                        .map(e -> e.getAsJsonPrimitive().getAsString())
                        .collect(Collectors.toList());
                textureString = textureArray.get(0);
            }
        } else {
            textureString = Genesis.MODID + ":" + id;
        }

        if (json.has(Props.textureLayout)) {
            final JsonElement tlEl = json.get(Props.textureLayout);
            if (tlEl.isJsonPrimitive() && tlEl.getAsJsonPrimitive().isString()) {
                final String layoutStr = tlEl.getAsJsonPrimitive().getAsString();
                textureLayout = layoutStr.codePoints()
                        .limit(6)
                        .map(Character::getNumericValue)
                        .map(i -> Math.min(i, 5)) // [0, 5]
                        .toArray();
            }
        }

        JsonUtils.consumeString(json, Props.title, s -> title = s);
        JsonUtils.consumeString(json, Props.desc, s -> description = s.split("\n"));

        if (json.has(Props.trap)) {
            final JsonElement e = json.get(Props.trap);
            if (e.isJsonObject())
                trapData = new TrapData(e.getAsJsonObject());
        }
    }

    private static class Props {
        static final String texture = "texture";
        static final String textureLayout = "texture_layout";
        static final String title = "title";
        static final String desc = "description";
        static final String trap = "trap";
    }
}
