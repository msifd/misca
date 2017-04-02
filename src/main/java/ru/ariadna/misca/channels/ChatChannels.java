package ru.ariadna.misca.channels;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class ChatChannels {
    private ChannelManager manager = new ChannelManager();
    private CommandChannel commandChannel = new CommandChannel(manager);
    private CommandSend commandSend = new CommandSend(manager);

    public void init(FMLServerStartingEvent event) {
        manager.init();
        event.registerServerCommand(commandChannel);
        event.registerServerCommand(commandSend);
    }
}
