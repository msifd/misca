package ru.ariadna.misca;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ariadna.misca.channels.ChatChannels;
import ru.ariadna.misca.charsheet.Charsheets;
import ru.ariadna.misca.commands.CommandMiscaCommon;
import ru.ariadna.misca.config.ConfigManager;
import ru.ariadna.misca.crabs.Crabs;
import ru.ariadna.misca.database.DBHandler;
import ru.ariadna.misca.gui.MiscaGuiHandler;
import ru.ariadna.misca.things.MiscaThings;
import ru.ariadna.misca.toolbox.Toolbox;
import ru.ariadna.misca.tweaks.MiningNerf;
import ru.ariadna.misca.tweaks.Tweaks;

@Mod(modid = "misca", version = "@VERSION@")
public class Misca {
    @SidedProxy(serverSide = "ru.ariadna.misca.crabs.Crabs", clientSide = "ru.ariadna.misca.crabs.CrabsClient")
    public static Crabs crabs;
    @SidedProxy(serverSide = "ru.ariadna.misca.things.MiscaThings", clientSide = "ru.ariadna.misca.things.MiscaThingsClient")
    public static MiscaThings miscaThings;
    @Mod.Instance
    private static Misca instance;
    private static Logger logger = LogManager.getLogger("Misca");
    private static EventBus eventBus = new EventBus();

    private DBHandler dbHandler = DBHandler.INSTANCE;
    private Tweaks tweaks = new Tweaks();
    private Toolbox toolbox = new Toolbox();
    private ChatChannels chatChannels = new ChatChannels();
    private Charsheets charsheets = new Charsheets();
    private MiningNerf miningNerf = new MiningNerf();

    public static Misca instance() {
        return instance;
    }

    public static EventBus eventBus() {
        return eventBus;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigManager.instance.initConfig(event);

        eventBus.register(MiscaGuiHandler.instance);
        eventBus.register(crabs);
        eventBus.register(miscaThings);
        eventBus.register(tweaks);
        eventBus.register(toolbox);
        eventBus.register(chatChannels);
        eventBus.register(charsheets);
        eventBus.register(miningNerf);

        if (FMLCommonHandler.instance().getSide().isServer()) {
            eventBus.register(dbHandler);
        }

        eventBus.post(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        eventBus.post(event);
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandMiscaCommon());

        eventBus.post(event);
    }

    @EventHandler
    public void serverStop(FMLServerStoppingEvent event) {
        eventBus.post(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        eventBus.post(event);
        logger.info("Misca is fully loaded! Bon appetit!");
    }
}
