package msifeed.misca;

import msifeed.misca.charsheet.CharsheetHandler;
import msifeed.misca.chatex.Chatex;
import msifeed.misca.cmd.RollCommand;
import msifeed.misca.genesis.Genesis;
import msifeed.sys.rpc.RpcChannel;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Misca.MODID, name = Misca.NAME, version = Misca.VERSION)
public class Misca {
    public static final String MODID = "misca";
    public static final String NAME = "Misca";
    public static final String VERSION = "2.0";

    public static RpcChannel RPC = new RpcChannel(Misca.MODID);

    private final Genesis genesis = new Genesis();
    private final Chatex chatex = new Chatex();

    private final CharsheetHandler charsheetHandler = new CharsheetHandler();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        genesis.preInit();
        charsheetHandler.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        chatex.init();

        charsheetHandler.init();
        MiscaPerms.register();

        if (FMLCommonHandler.instance().getSide().isClient())
            MiscaClient.INSTANCE.init();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        chatex.registerCommands(event);

        event.registerServerCommand(new RollCommand());
    }
}
