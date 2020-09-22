package msifeed.misca.chatex;

import msifeed.misca.Misca;
import msifeed.misca.chatex.cmd.GlobalCommand;
import msifeed.misca.chatex.cmd.WhisperCommand;
import msifeed.misca.chatex.cmd.YellCommand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class ChatexServer implements IChatexProxy {
    @Override
    public void init() {
        Misca.RPC.register(new ChatexServerRpc());
        MinecraftForge.EVENT_BUS.register(new ChatMsgHandler());
    }

    @Override
    public void registerCommands(FMLServerStartingEvent event) {
        event.registerServerCommand(new GlobalCommand());
        event.registerServerCommand(new WhisperCommand());
        event.registerServerCommand(new YellCommand());
    }
}
