package ru.ariadna.misca.channels;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import ru.ariadna.misca.Misca;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class ChannelProvider {
    private File dataFile;
    private Toml toml = new Toml();
    private TomlWriter tomlWriter = new TomlWriter();
    private ChannelsConfigContent configContent = new ChannelsConfigContent();

    void init() {
        dataFile = new File(Misca.config.config_dir, "channels.toml");
        reloadConfigFile();
    }

    boolean channelExist(String channel) {
        return configContent.channels.containsKey(channel);
    }

    Channel getChannel(String channel) throws ChannelsException {
        Channel ch = configContent.channels.get(channel);
        if (ch == null) throw new ChannelsException(ChannelsException.Type.UNKNOWN_CHANNEL);
        return ch;
    }

    Map<String, Channel> getChannels() {
        return configContent.channels;
    }

    void updateChannel(Channel ch) {
        configContent.channels.put(ch.name, ch);
        updateConfigFile();
    }

    void removeChannel(Channel ch) {
        if (configContent.channels.remove(ch.name) != null) {
            updateConfigFile();
        }
    }

    void reloadConfigFile() {
        try {
            configContent = toml.read(dataFile).to(ChannelsConfigContent.class);
            // Fix empty config file
            if (configContent.channels == null) {
                configContent.channels = new HashMap<>();
            }
        } catch (IllegalStateException e) {
            ChatChannels.logger.error("Error while reading channels config! {}", e);
        }
    }

    private void updateConfigFile() {
        try {
            tomlWriter.write(configContent, dataFile);
        } catch (IOException e) {
            ChatChannels.logger.error("Failed to write channels data! {}", e.toString());
        }
    }

    private class ChannelsConfigContent {
        Map<String, Channel> channels = new HashMap<>();
    }
}
