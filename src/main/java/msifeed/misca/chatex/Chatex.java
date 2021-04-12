package msifeed.misca.chatex;

import msifeed.misca.Misca;
import msifeed.misca.chatex.client.ChatGuiHandler;
import msifeed.misca.chatex.client.ChatexClientLogs;
import msifeed.misca.chatex.client.NametagHandler;
import msifeed.misca.chatex.client.TypingGuiHandler;
import msifeed.misca.chatex.cmd.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class Chatex {
    public void init() {
        Misca.RPC.register(new ChatexRpc());

        if (FMLCommonHandler.instance().getSide().isClient()) {
            MinecraftForge.EVENT_BUS.register(new ChatGuiHandler());
            MinecraftForge.EVENT_BUS.register(new TypingGuiHandler());
            MinecraftForge.EVENT_BUS.register(new NametagHandler());

            ChatexClientLogs.init();
        }
    }

    public void registerCommands(FMLServerStartingEvent event) {
        event.registerServerCommand(new OfftopCommand());
        event.registerServerCommand(new GlobalCommand());

        event.registerServerCommand(new GmGlobalCommand());
        event.registerServerCommand(new GmPmCommand());
        event.registerServerCommand(new GmSayCommand());
    }
}
