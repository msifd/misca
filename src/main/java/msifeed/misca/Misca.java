package msifeed.misca;

import msifeed.misca.charsheet.cap.CharsheetHandler;
import msifeed.misca.chatex.Chatex;
import msifeed.misca.cmd.MiscaCommand;
import msifeed.misca.cmd.RollCommand;
import msifeed.misca.combat.Combat;
import msifeed.misca.combat.CombatCommand;
import msifeed.misca.combat.cap.CombatantHandler;
import msifeed.misca.environ.Environ;
import msifeed.misca.environ.EnvironCommand;
import msifeed.misca.locks.Locks;
import msifeed.misca.locks.LocksCommand;
import msifeed.misca.rename.RenameCommand;
import msifeed.misca.rename.RenameItems;
import msifeed.misca.supplies.BackgroundSupplies;
import msifeed.misca.supplies.InvoiceCommand;
import msifeed.sys.rpc.RpcChannel;
import msifeed.sys.sync.SyncChannel;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.nio.file.Paths;

@Mod(modid = Misca.MODID, name = Misca.NAME, version = Misca.VERSION)
public class Misca {
    public static final String MODID = "misca";
    public static final String NAME = "Misca";
    public static final String VERSION = "2.0";

    public static final RpcChannel RPC = new RpcChannel(MODID + ":rpc");
    public static final SyncChannel<MiscaSharedConfig> SHARED
            = new SyncChannel<>(RPC, Paths.get(MODID, "shared.json"), MiscaSharedConfig.class);

    private final Chatex chatex = new Chatex();
    private final Combat combat = new Combat();
    private final Environ environ = new Environ();
    private final Locks locks = new Locks();
    private final BackgroundSupplies supplies = new BackgroundSupplies();

    private final CharsheetHandler charsheetHandler = new CharsheetHandler();
    private final CombatantHandler combatantHandler = new CombatantHandler();

    public static MiscaSharedConfig getSharedConfig() {
        return SHARED.get();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MiscaThings.init();
        charsheetHandler.preInit();
        combatantHandler.preInit();
        locks.preInit();
        supplies.preInit();

        if (FMLCommonHandler.instance().getSide().isClient())
            MiscaClient.INSTANCE.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        chatex.init();
        combat.init();
        environ.init();
        locks.init();

        charsheetHandler.init();
        MiscaPerms.register();
        RenameItems.register();

        if (FMLCommonHandler.instance().getSide().isClient())
            MiscaClient.INSTANCE.init();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        try {
            SHARED.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        chatex.registerCommands(event);
        event.registerServerCommand(new MiscaCommand());
        event.registerServerCommand(new RollCommand());
        event.registerServerCommand(new RenameCommand());
        event.registerServerCommand(new EnvironCommand());
        event.registerServerCommand(new CombatCommand());
        event.registerServerCommand(new LocksCommand());
        event.registerServerCommand(new InvoiceCommand());
    }
}
