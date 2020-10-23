package msifeed.misca;

import msifeed.misca.charsheet.CharsheetHandler;
import msifeed.misca.chatex.Chatex;
import msifeed.misca.cmd.RollCommand;
import msifeed.misca.combat.Combat;
import msifeed.misca.environ.Environ;
import msifeed.misca.environ.EnvironCommand;
import msifeed.misca.genesis.Genesis;
import msifeed.misca.rename.RenameCommand;
import msifeed.misca.rename.RenameItems;
import msifeed.misca.supplies.InvoiceCommand;
import msifeed.sys.rpc.RpcChannel;
import msifeed.sys.sync.SyncChannel;
import net.minecraft.util.ResourceLocation;
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

    public static RpcChannel RPC = new RpcChannel(new ResourceLocation(MODID, "rpc"));
    public static SyncChannel SYNC = new SyncChannel(RPC, "sync");

    private final Genesis genesis = new Genesis();
    private final Chatex chatex = new Chatex();
    private final Combat combat = new Combat();
    private final Environ environ = new Environ();

    private final CharsheetHandler charsheetHandler = new CharsheetHandler();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        genesis.preInit();
        charsheetHandler.preInit();

        if (FMLCommonHandler.instance().getSide().isClient())
            MiscaClient.INSTANCE.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        chatex.init();
        combat.init();
        environ.init();

        charsheetHandler.init();
        MiscaPerms.register();
        RenameItems.register();

        if (FMLCommonHandler.instance().getSide().isClient())
            MiscaClient.INSTANCE.init();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        chatex.registerCommands(event);

        event.registerServerCommand(new RollCommand());
        event.registerServerCommand(new InvoiceCommand());
        event.registerServerCommand(new RenameCommand());
        event.registerServerCommand(new EnvironCommand());
    }
}
