package ru.ariadna.misca.channels;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ariadna.misca.config.ConfigReloadEvent;

public class ChatChannels {
    static Logger logger = LogManager.getLogger("Misca-Channels");

    private ChannelProvider provider = new ChannelProvider();
    private ChannelManager manager = new ChannelManager(provider);
    private CommandChannel commandChannel = new CommandChannel(manager);
    private CommandChannelMessage commandChannelMessage = new CommandChannelMessage(manager);
    private CommandLinkMessage commandLinkMessage = new CommandLinkMessage(manager);

    @Subscribe
    public void onPreInit(FMLPreInitializationEvent event) {
        provider.init();
    }

    @Subscribe
    public void init(FMLServerStartingEvent event) {
        event.registerServerCommand(commandChannel);
        event.registerServerCommand(commandChannelMessage);
        event.registerServerCommand(commandLinkMessage);
    }

    @Subscribe
    public void onReloadEvent(ConfigReloadEvent event) {
        provider.reloadConfigFile();
    }
}
