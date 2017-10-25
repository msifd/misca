package msifeed.mc.misca.config;

import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cpw.mods.fml.common.FMLCommonHandler;

import java.io.*;
import java.lang.reflect.Constructor;

public class JsonConfig<T> {
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Class<T> configClass;
    private T configObject;
    private String filename;
    private boolean isServerSided = false;

    JsonConfig(Class<T> type, String filename) {
        this.configClass = type;
        this.configObject = getDefaultConfig();
        this.filename = filename;
    }

    void setServerSided() {
        isServerSided = true;
    }

    public T get() {
        return configObject;
    }

    @Subscribe
    public void onReloadEvent(ConfigEvent.Reload event) {
        if (isServerSided && FMLCommonHandler.instance().getSide().isClient()) return;

        File configFile = getConfigFile();
        read(configFile);

        if (configObject == null) configObject = getDefaultConfig();

        write(configFile);
    }

    @Subscribe
    public void onCollectEvent(ConfigEvent.Collect event) {
        if (isServerSided || FMLCommonHandler.instance().getSide().isClient()) return;
        event.configs.put(filename, gson.toJson(configObject));
    }

    @Subscribe
    public void onOverrideEvent(ConfigEvent.Override event) {
        if (isServerSided) return;
        configObject = gson.fromJson(event.configs.get(filename), configClass);
    }

    private void read(File configFile) {
        if (!configFile.exists()) return;

        try {
            configObject = gson.fromJson(new FileReader(configFile), configClass);
        } catch (IOException e) {
            ConfigManager.logger.error("Error while reading '{}' config: {}", filename, e.getMessage());
        }
    }

    private void write(File configFile) {
        try (Writer writer = new FileWriter(configFile)) {
            gson.toJson(configObject, writer);
        } catch (IOException e) {
            ConfigManager.logger.error("Error while writing '{}' config: {}", filename, e.getMessage());
        }
    }

    private File getConfigFile() {
        return new File(ConfigManager.config_dir, filename);
    }

    private T getDefaultConfig() {
        try {
            Constructor<T> constructor = configClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            ConfigManager.logger.error("Getting default config '{}' : {}", filename, e.getMessage());
            throw new RuntimeException("Failed to get default config for " + configClass.getName());
        }
    }
}
