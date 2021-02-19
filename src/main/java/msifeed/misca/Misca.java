package msifeed.misca;

import com.google.gson.reflect.TypeToken;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.charstate.Charstate;
import msifeed.misca.charstate.EffortsCommand;
import msifeed.misca.charstate.NeedsCommand;
import msifeed.misca.charstate.rolls.RollRpc;
import msifeed.misca.chatex.Chatex;
import msifeed.misca.client.MiscaClient;
import msifeed.misca.cmd.MiscaCommand;
import msifeed.misca.cmd.RollCommand;
import msifeed.misca.combat.Combat;
import msifeed.misca.combat.CombatCommand;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.nio.file.Paths;

@Mod(modid = Misca.MODID, name = Misca.NAME)
public class Misca {
    public static final String MODID = "misca";
    public static final String NAME = "Misca";

    public static final RpcChannel RPC = new RpcChannel(MODID + ":rpc");
    public static final SyncChannel<MiscaSharedConfig> SHARED
            = new SyncChannel<>(RPC, Paths.get(MODID, "shared.json"), TypeToken.get(MiscaSharedConfig.class));

    private final Chatex chatex = new Chatex();
    private final Combat combat = new Combat();
    private final Environ environ = new Environ();
    private final Locks locks = new Locks();
    private final BackgroundSupplies supplies = new BackgroundSupplies();
    private final Charstate charstate = new Charstate();

    public static MiscaSharedConfig getSharedConfig() {
        return SHARED.get();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

        MiscaThings.init();
        CharsheetProvider.preInit();
        combat.preInit();
        locks.preInit();
        supplies.preInit();
        charstate.preInit();

        Misca.RPC.register(new RollRpc());

        if (FMLCommonHandler.instance().getSide().isClient())
            MiscaClient.INSTANCE.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ConfigManager.sync(MODID, Config.Type.INSTANCE);

        chatex.init();
        combat.init();
        environ.init();

        RenameItems.register();

        if (FMLCommonHandler.instance().getSide().isClient())
            MiscaClient.INSTANCE.init();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        try {
            SHARED.sync();
            Combat.sync();
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
        event.registerServerCommand(new NeedsCommand());
        event.registerServerCommand(new EffortsCommand());
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }

    @SubscribeEvent
    public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(MODID))
            ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }
}
