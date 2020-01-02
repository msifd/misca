package msifeed.mc.misca;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import msifeed.mc.aorta.genesis.Genesis;
import msifeed.mc.aorta.locks.Locks;
import msifeed.mc.aorta.sys.rpc.Rpc;
import msifeed.mc.misca.books.RemoteBookManager;
import msifeed.mc.misca.commands.CommandMiscaCommon;
import msifeed.mc.misca.config.ConfigManager;
import msifeed.mc.misca.crabs.Crabs;
import msifeed.mc.misca.database.DBHandler;
import msifeed.mc.misca.tweaks.DRM;
import msifeed.mc.misca.tweaks.GameWindowOptions;
import msifeed.mc.misca.tweaks.Tweaks;
import msifeed.mc.misca.tweaks.mining.MiningNerf;
import msifeed.mc.misca.utils.MiscaGuiHandler;
import msifeed.mc.misca.utils.MiscaNetwork;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "misca", version = "@VERSION@")
public class Misca {
    @Mod.Instance
    public static Misca INSTANCE;

    @SidedProxy(
            serverSide = "msifeed.mc.aorta.genesis.Genesis",
            clientSide = "msifeed.mc.aorta.genesis.GenesisClient"
    )
    public static Genesis GENESIS;

    @SidedProxy(
            serverSide = "msifeed.mc.misca.crabs.Crabs",
            clientSide = "msifeed.mc.misca.crabs.CrabsClient"
    )
    public static Crabs crabs;

    public static Tweaks tweaks = new Tweaks();
    private static Logger logger = LogManager.getLogger("Misca");
    private MiningNerf miningNerf = new MiningNerf();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (FMLCommonHandler.instance().getSide().isServer()) {
            ConfigManager.INSTANCE.eventbus.register(DBHandler.INSTANCE);
        }

        ConfigManager.INSTANCE.init(event);

        if (FMLCommonHandler.instance().getSide().isClient()) {
            GameWindowOptions.preInit();
        }

        crabs.preInit(event);
        tweaks.preInit(event);
        miningNerf.preInit(event);
        RemoteBookManager.INSTANCE.preInit(event);

        ConfigManager.INSTANCE.reloadConfig();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        GENESIS.init();
        crabs.init(event);
        Locks.init();
        tweaks.onInit(event);
        miningNerf.onInit(event);
        MiscaNetwork.INSTANCE.onInit();
        RemoteBookManager.INSTANCE.init(event);

        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new MiscaGuiHandler());

        if (FMLCommonHandler.instance().getSide().isClient()) {
            DRM.apply();
        }
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandMiscaCommon());

        tweaks.onServerStart(event);
        miningNerf.onServerStart(event);
    }

    @EventHandler
    public void serverStop(FMLServerStoppingEvent event) {
        miningNerf.onServerStop(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        miningNerf.onPostInit(event);

        logger.info("Misca is fully loaded! Bon appetit!");
    }
}
