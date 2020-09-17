package msifeed.misca;

import msifeed.misca.chatex.IChatexProxy;
import msifeed.misca.cmd.RollCommand;
import msifeed.misca.names.NamesExtension;
import msifeed.sys.rpc.RpcChannel;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Misca.MODID, name = Misca.NAME, version = Misca.VERSION)
public class Misca {
    public static final String MODID = "misca";
    public static final String NAME = "Misca";
    public static final String VERSION = "2.0";

    public static RpcChannel RPC = new RpcChannel(Misca.MODID + ".rpc");

    @SidedProxy(clientSide = "msifeed.misca.chatex.client.ChatexClient", serverSide = "msifeed.misca.chatex.server.ChatexServer")
    private static IChatexProxy chatex;

    private final NamesExtension names = new NamesExtension();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        chatex.init();
        names.init();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        chatex.registerCommands(event);

        event.registerServerCommand(new RollCommand());
    }
}
