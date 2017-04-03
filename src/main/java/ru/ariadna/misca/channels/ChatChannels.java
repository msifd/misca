package ru.ariadna.misca.channels;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatChannels {
    static Logger logger = LogManager.getLogger("Misca-Channels");

    private ChannelProvider provider = new ChannelProvider();
    private ChannelManager manager = new ChannelManager(provider);
    private CommandChannel commandChannel = new CommandChannel(manager);
    private CommandChannelMessage commandChannelMessage = new CommandChannelMessage(manager);
    private CommandLinkMessage commandLinkMessage = new CommandLinkMessage(manager);

    public void init(FMLServerStartingEvent event) {
        provider.init();
        event.registerServerCommand(commandChannel);
        event.registerServerCommand(commandChannelMessage);
        event.registerServerCommand(commandLinkMessage);
    }
}
