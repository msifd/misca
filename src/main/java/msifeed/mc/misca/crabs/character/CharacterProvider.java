package msifeed.mc.misca.crabs.character;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.reflect.TypeToken;
import msifeed.mc.misca.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public enum CharacterProvider {
    INSTANCE;

    private static Logger logger = LogManager.getLogger("Crabs.Chars");
    private final Type contentType = new TypeToken<Map<UUID, Character>>() {
    }.getType();
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(
                    new TypeToken<EnumMap<Stats, Integer>>() {
                    }.getType(),
                    new StatsMapInstanceCreator()
            )
            .create();
    private File charsFile;

    public void preInit() {
        charsFile = new File(ConfigManager.config_dir, "characters.json");
    }

    public synchronized void save(Map<UUID, Character> chars) {
        logger.info("Saving {} characters...", chars.size());
        try {
            final String json = gson.toJson(chars, contentType);
            Files.write(charsFile.toPath(), json.getBytes(Charsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized Map<UUID, Character> load() {
        if (charsFile.exists()) {
            logger.info("Loading characters...");
            try {
                final String json = new String(Files.readAllBytes(charsFile.toPath()), Charsets.UTF_8);
                final Map<UUID, Character> chars = gson.fromJson(json, contentType);
                logger.info("Loaded {} characters!", chars.size());
                return chars;
            } catch (Exception e) {
                logger.error("Failed to load characters! Cause: `{}`", e.getMessage());
            }
        } else {
            logger.info("Characters file is not exists. Skip.");
        }
        return Maps.newHashMap();
    }

    private class StatsMapInstanceCreator implements InstanceCreator<EnumMap<Stats, Integer>> {
        @Override
        public EnumMap<Stats, Integer> createInstance(final Type type) {
            return new EnumMap<>(Stats.class);
        }
    }
}
