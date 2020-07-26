package msifeed.misca.chatex.server;

import msifeed.misca.Misca;
import msifeed.misca.chatex.IChatexProxy;
import msifeed.misca.chatex.server.cmd.GlobalCommand;
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
    }
}
