package ru.ariadna.misca;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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

@Mod(modid = "misca", version = "@VERSION@", dependencies = "required-after:llibrary@[1.5.1,)")
public class Misca {
    @Mod.Instance
    private static Misca instance;

    private static Logger logger = LogManager.getLogger("Misca");
    private static EventBus eventBus = new EventBus();

    private DBHandler dbHandler = new DBHandler();
    private Crabs crabs = Crabs.instance;
    private Tweaks tweaks = new Tweaks();
    private Toolbox toolbox = new Toolbox();
    private MiscaThings miscaThings = new MiscaThings();
    private ChatChannels chatChannels = new ChatChannels();
    private Charsheets charsheets = new Charsheets();
    private MiningNerf miningNerf = new MiningNerf();

    public Misca() {
        eventBus.register(MiscaGuiHandler.instance);
        eventBus.register(crabs);
        eventBus.register(tweaks);
        eventBus.register(toolbox);
        eventBus.register(miscaThings);
        eventBus.register(chatChannels);
        eventBus.register(charsheets);
        eventBus.register(miningNerf);

        if (FMLCommonHandler.instance().getSide().isServer()) {
            eventBus.register(dbHandler);
        }
    }

    public static Misca instance() {
        return instance;
    }

    public static EventBus eventBus() {
        return eventBus;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigManager.instance.initConfig(event);

        eventBus.post(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        eventBus.post(event);
    }

    @EventHandler
    @SideOnly(Side.SERVER)
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandMiscaCommon());

        eventBus.post(event);
    }

    @EventHandler
    @SideOnly(Side.SERVER)
    public void serverStop(FMLServerStoppingEvent event) {
        eventBus.post(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        eventBus.post(event);
        logger.info("Misca is fully loaded! Bon appetit!");
    }
}
