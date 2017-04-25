package ru.ariadna.misca.tweaks;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import ru.ariadna.misca.Misca;

import java.io.File;
import java.io.IOException;

class TweaksConfig {
    private File configFile;
    private Toml toml = new Toml();
    private TomlWriter tomlWriter = new TomlWriter();
    private ConfigFileContent configContent;

    ConfigFileContent config() {
        return configContent;
    }

    void init() {
        configFile = new File(Misca.config_dir, "tweaks.toml");
        load();
    }

    private void load() {
        if (!configFile.exists()) {
            Tweaks.logger.info("No tweaks config file! Creating a default one.");
            writeDummy();
        }

        try {
            configContent = toml.read(configFile).to(ConfigFileContent.class);
            // Fix empty config file
            if (configContent.slow_mining == null) {
                Tweaks.logger.warn("Tweaks config is empty! Using defaults.");
                configContent = new ConfigFileContent();
                writeDummy();
            }
        } catch (IllegalStateException e) {
            Tweaks.logger.error("Error while reading tweaks config! {}", e);
        }
    }

    private void writeDummy() {
        try {
            tomlWriter.write(new ConfigFileContent(), configFile);
        } catch (IOException e) {
            Tweaks.logger.error("Failed to write tweaks dummy config! {}", e.toString());
        }
    }

    class ConfigFileContent {
        public SlowMining.ConfigSection slow_mining = new SlowMining.ConfigSection();
    }
}
