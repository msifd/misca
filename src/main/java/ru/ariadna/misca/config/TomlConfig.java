package ru.ariadna.misca.config;

import com.google.common.eventbus.Subscribe;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ariadna.misca.Misca;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;

public class TomlConfig<T extends Serializable> {
    private static Logger logger = LogManager.getLogger("Misca-Config");
    private String filename;
    private Class<T> config_class;
    private T config_object;
    private boolean syncEnabled = true;

    public TomlConfig(Class<T> clazz, String filename) {
        this.config_class = clazz;
        this.filename = filename;

        Misca.eventBus().register(this);
    }

    @Subscribe
    public void onReloadEvent(ConfigReloadEvent event) {
        if (FMLCommonHandler.instance().getSide().isServer())
            read();
        else if (config_object == null)
            config_object = getConfigDefault();
    }

    @Subscribe
    public void onSyncEvent(ConfigSyncEvent event) {
        if (!syncEnabled) return;

        if (FMLCommonHandler.instance().getSide().isServer()) {
            event.configs.put(filename, config_object);
        } else {
            Object kinda_config = event.configs.get(filename);
            if (config_class.isInstance(kinda_config))
                config_object = config_class.cast(kinda_config);
            else
                logger.error("Sync config type mismatch for '{}'! Expected '{}' got '{}'",
                        filename, config_class.getSimpleName(), kinda_config.getClass().getSimpleName());
        }
    }

    private void read() {
        File configFile = getConfigFile();

        if (!configFile.exists()) {
            write();
            return;
        }

        try {
            // Such defaults wow
            TomlWriter tomlWriter = new TomlWriter();
            Toml toml_default = new Toml().read(tomlWriter.write(getConfigDefault()));

            config_object = new Toml(toml_default).read(configFile).to(config_class);
            write();
        } catch (IllegalStateException e) {
            logger.error("Error while reading '{}' config!", config_class.getSimpleName());
        }
    }

    private void write() {
        File configFile = getConfigFile();

        if (config_object == null)
            config_object = getConfigDefault();

        try {
            TomlWriter tomlWriter = new TomlWriter();
            tomlWriter.write(config_object, configFile);
        } catch (IOException e) {
            logger.error("Error while writing '{}' config! {}", e.toString());
        }
    }

    public T get() {
        return config_object;
    }

    public void setSync(boolean val) {
        this.syncEnabled = val;
    }

    private File getConfigFile() {
        return new File(ConfigManager.config_dir, filename);
    }

    private T getConfigDefault() {
        try {
            Constructor<T> constructor = config_class.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            logger.error("Getting default config '{}' : {}", filename, e.getMessage());
            throw new RuntimeException("Failed to get default config for " + config_class.getName());
        }
    }
}
