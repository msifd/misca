package msifeed.mc.misca.crabs.character;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import msifeed.mc.misca.config.ConfigManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

public enum CharacterProvider {
    INSTANCE;

    private File charsFile;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void preInit(FMLPreInitializationEvent event) {
        charsFile = new File(ConfigManager.config_dir, "characters.json");
    }

    public void save(Map<UUID, Character> chars) {
        try {
            Content content = new Content();
            content.chars = chars;
            final String json = gson.toJson(content);
            Files.write(charsFile.toPath(), json.getBytes(Charsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<UUID, Character> load() {
        try {
            final String json = new String(Files.readAllBytes(charsFile.toPath()), Charsets.UTF_8);
            return gson.fromJson(json, Content.class).chars;
        } catch (IOException e) {
            e.printStackTrace();
            return Maps.newHashMap();
        }
    }

    private static class Content {
        Map<UUID, Character> chars;
    }
}
