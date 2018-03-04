package msifeed.mc.misca.crabs.action;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import msifeed.mc.misca.config.ConfigManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public enum ActionProvider {
    INSTANCE;

    private static Logger logger = ActionManager.logger;
    private final Type contentType = new TypeToken<List<Action>>() {
    }.getType();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Action.class, new ActionJsonSerializer())
            .setPrettyPrinting()
            .create();

    private File actionsFile;

    public void preInit() {
        actionsFile = new File(ConfigManager.config_dir, "actions.json");
    }

    public List<Action> load() {
        if (!actionsFile.exists()) {
            logger.info("Actions file not exists. Adding a default one.");
            saveDefault();
        }

        logger.info("Loading actions...");
        final List<Action> actions = new ArrayList<>();
        try {
            final String json = new String(Files.readAllBytes(actionsFile.toPath()), Charsets.UTF_8);
            actions.addAll(gson.fromJson(json, contentType));
            logger.info("Loaded {} actions!", actions.size());
        } catch (Exception e) {
            logger.error("Failed to load actions! Cause: `{}`", e.getMessage());
        }

        return actions;
    }

    private void saveDefault() {
        final List<Action> defaultActions = Lists.newArrayList();
        try {
            final String json = gson.toJson(defaultActions, contentType);
            Files.write(actionsFile.toPath(), json.getBytes(Charsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
