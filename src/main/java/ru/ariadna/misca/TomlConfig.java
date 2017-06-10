package ru.ariadna.misca;

import com.google.common.eventbus.Subscribe;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ariadna.misca.events.MiscaReloadEvent;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class TomlConfig<T> {
    private static Logger logger = LogManager.getLogger("Misca-Config");
    private String filename;
    private Class<T> config_class;
    private T config_object;

    private Toml toml_default;

    public TomlConfig(Class<T> clazz, String filename) {
        this.config_class = clazz;
        this.filename = filename;

        // Such defaults wow
        TomlWriter tomlWriter = new TomlWriter();
        toml_default = new Toml().read(tomlWriter.write(getConfigDefault()));

        Misca.eventBus().register(this);
    }

    @Subscribe
    public void onReloadEvent(MiscaReloadEvent event) {
        read();
    }

    @Subscribe
    public void onServerStop(FMLServerStoppingEvent event) {
        write();
    }

    public void read() {
        File configFile = getConfigFile();

        if (!configFile.exists()) {
            config_object = getConfigDefault();
            write();
            return;
        }

        try {
            config_object = new Toml(toml_default).read(configFile).to(config_class);
            write();
        } catch (IllegalStateException e) {
            logger.error("Error while reading '{}' config!", config_class.getSimpleName());
        }
    }

    public void write() {
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
        if (config_object == null) read();
        return config_object;
    }

    private File getConfigFile() {
        return new File(Misca.config_dir, filename);
    }

    private T getConfigDefault() {
        try {
            Constructor<T> constructor = config_class.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            logger.error("Getting dummy config", e);
        }
        return null;
    }

    private boolean fixNullFields(T config) {
        T dummy = getConfigDefault();
        boolean has_null_fields = false;

        try {
            for (Field f : config_class.getDeclaredFields()) {
                f.setAccessible(true);
                Object dummy_value = f.get(dummy);
                System.out.println(f.getName() + f.get(config));
                if (f.get(config) == null && dummy_value != null) {
                    f.set(config, dummy_value);
                    has_null_fields = true;
                }
            }
        } catch (ReflectiveOperationException e) {
            logger.error("Fixing null fields", e);
        }

        return has_null_fields;
    }
}
