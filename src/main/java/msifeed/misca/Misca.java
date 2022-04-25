package msifeed.misca;

import com.google.gson.reflect.TypeToken;
import msifeed.misca.charsheet.EffectsHandler;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.chatex.Chatex;
import msifeed.misca.client.MiscaClient;
import msifeed.misca.cmd.*;
import msifeed.misca.environ.Environ;
import msifeed.misca.environ.EnvironCommand;
import msifeed.misca.locks.Locks;
import msifeed.misca.logdb.LogDB;
import msifeed.misca.potions.OtherPotions;
import msifeed.misca.regions.CommandRegions;
import msifeed.misca.regions.RegionControl;
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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = Misca.MODID, name = Misca.NAME)
public class Misca {
    public static final String MODID = "misca";
    public static final String NAME = "Misca";

    public static final RpcChannel RPC = new RpcChannel(MODID + ":rpc");
    public static final SyncChannel<MiscaSharedConfig> SHARED
            = new SyncChannel<>(RPC, "shared.json", TypeToken.get(MiscaSharedConfig.class));
    public static final SyncChannel<EffectsHandler.ItemEffectsConfig> ITEM_EFFECTS
            = new SyncChannel<>(RPC, "item_effects.json", TypeToken.get(EffectsHandler.ItemEffectsConfig.class));

    private final Chatex chatex = new Chatex();
    private final Environ environ = new Environ();
    private final Locks locks = new Locks();
    private final BackgroundSupplies supplies = new BackgroundSupplies();

    public static MiscaSharedConfig getSharedConfig() {
        return SHARED.get();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

        MiscaThings.init();
        CharsheetProvider.preInit();
        locks.preInit();
        supplies.preInit();
        RegionControl.init();

        MinecraftForge.EVENT_BUS.register(OtherPotions.class);

        if (FMLCommonHandler.instance().getSide().isClient()) {
            MiscaClient.INSTANCE.preInit();
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ConfigManager.sync(MODID, Config.Type.INSTANCE);

        chatex.init();
        environ.init();

        RenameItems.register();

        if (FMLCommonHandler.instance().getSide().isClient())
            MiscaClient.INSTANCE.init();
    }

    public static void syncConfig() throws Exception {
        SHARED.sync();
        ITEM_EFFECTS.sync();
        LogDB.reload();
        RegionControl.sync();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        try {
            syncConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }

        chatex.registerCommands(event);
        event.registerServerCommand(new MiscaCommand());
        event.registerServerCommand(new RollCommand());
        event.registerServerCommand(new RenameCommand());
        event.registerServerCommand(new EnvironCommand());
        event.registerServerCommand(new LocksCommand());
        event.registerServerCommand(new InvoiceCommand());
        event.registerServerCommand(new SkillsCommand());
        event.registerServerCommand(new BlessCommand());
        event.registerServerCommand(new UnstuckCommand());
        event.registerServerCommand(new CommandRegions());
        event.registerServerCommand(new CharsheetCommand());
    }

    @SubscribeEvent
    public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(MODID))
            ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }
}
