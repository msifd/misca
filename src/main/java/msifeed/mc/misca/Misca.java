package msifeed.mc.misca;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import msifeed.mc.misca.commands.CommandMiscaCommon;
import msifeed.mc.misca.config.ConfigManager;
import msifeed.mc.misca.crabs.Crabs;
import msifeed.mc.misca.database.DBHandler;
import msifeed.mc.misca.things.MiscaThings;
import msifeed.mc.misca.tweaks.Tweaks;
import msifeed.mc.misca.tweaks.mining.MiningNerf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "misca", version = "@VERSION@")
public class Misca {
    public static final EventBus eventbus = new EventBus();
    private static Logger logger = LogManager.getLogger("Misca");

    @SidedProxy(
            serverSide = "msifeed.mc.misca.crabs.Crabs",
            clientSide = "msifeed.mc.misca.crabs.CrabsClient"
    )
    public static Crabs crabs;
    @SidedProxy(
            serverSide = "msifeed.mc.misca.things.MiscaThings",
            clientSide = "msifeed.mc.misca.things.MiscaThingsClient"
    )
    public static MiscaThings miscaThings;
    public static Tweaks tweaks = new Tweaks();

    private MiningNerf miningNerf = new MiningNerf();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigManager.INSTANCE.init(event);

        eventbus.register(tweaks);
        eventbus.register(miscaThings);
        eventbus.register(crabs);

        eventbus.register(miningNerf);

//        eventbus.register(MiscaGuiHandler.instance);
//        eventbus.register(walkieTalkie);
//        eventbus.register(toolbox);
//        eventbus.register(chatChannels);
//        eventbus.register(charsheets);

        if (FMLCommonHandler.instance().getSide().isServer()) {
            eventbus.register(DBHandler.INSTANCE);
        }

        eventbus.post(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        eventbus.post(event);
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandMiscaCommon());

        eventbus.post(event);
    }

    @EventHandler
    public void serverStop(FMLServerStoppingEvent event) {
        eventbus.post(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        eventbus.post(event);
        logger.info("Misca is fully loaded! Bon appetit!");
    }
}
