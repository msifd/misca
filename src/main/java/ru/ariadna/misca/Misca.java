package ru.ariadna.misca;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ariadna.misca.blocks.MiscaBlocks;
import ru.ariadna.misca.channels.ChatChannels;
import ru.ariadna.misca.charsheet.Charsheets;
import ru.ariadna.misca.database.DBHandler;
import ru.ariadna.misca.events.MiscaReloadEvent;
import ru.ariadna.misca.toolbox.Toolbox;
import ru.ariadna.misca.tweaks.MiningNerf;
import ru.ariadna.misca.tweaks.Tweaks;

import java.io.File;

@Mod(modid = "misca", version = "@VERSION@")
public class Misca {
    public static File config_dir;
    static Logger logger = LogManager.getLogger("Misca");
    private static EventBus eventBus = new EventBus();
    private DBHandler dbHandler = new DBHandler();
    private Tweaks tweaks = new Tweaks();
    private Toolbox toolbox = new Toolbox();
    private MiscaBlocks miscaBlocks = new MiscaBlocks();
    private ChatChannels chatChannels = new ChatChannels();
    private Charsheets charsheets = new Charsheets();
    private MiningNerf miningNerf = new MiningNerf();

    public Misca() {
        eventBus.register(dbHandler);
        eventBus.register(tweaks);
        eventBus.register(toolbox);
        eventBus.register(miscaBlocks);
        eventBus.register(chatChannels);
        eventBus.register(charsheets);
        eventBus.register(miningNerf);
    }

    public static EventBus eventBus() {
        return eventBus;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config_dir = new File(event.getModConfigurationDirectory(), "misca");
        config_dir.mkdirs();

        eventBus.post(new MiscaReloadEvent());
        eventBus.post(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        eventBus.post(event);
    }

    @EventHandler
    @SideOnly(Side.SERVER)
    public void serverStart(FMLServerStartingEvent event) {
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
