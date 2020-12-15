package msifeed.misca.chatex;

import msifeed.misca.Misca;
import msifeed.misca.chatex.client.NametagHandler;
import msifeed.misca.chatex.client.TypingGuiHandler;
import msifeed.misca.chatex.client.gui.ChatGuiHandler;
import msifeed.misca.chatex.cmd.GlobalCommand;
import msifeed.misca.chatex.cmd.WhisperCommand;
import msifeed.misca.chatex.cmd.YellCommand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class Chatex {
    public void init() {
        Misca.RPC.register(new ChatexRpc());
        MinecraftForge.EVENT_BUS.register(new ChatMsgHandler());

        if (FMLCommonHandler.instance().getSide().isClient()) {
            MinecraftForge.EVENT_BUS.register(new ChatGuiHandler());
            MinecraftForge.EVENT_BUS.register(new TypingGuiHandler());
            MinecraftForge.EVENT_BUS.register(new NametagHandler());
        }
    }

    public void registerCommands(FMLServerStartingEvent event) {
        event.registerServerCommand(new GlobalCommand());
        event.registerServerCommand(new WhisperCommand());
        event.registerServerCommand(new YellCommand());
    }
}
