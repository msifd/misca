package ru.ariadna.misca;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ariadna.misca.channels.ChatChannels;
import ru.ariadna.misca.charsheet.Charsheets;
import ru.ariadna.misca.database.DBHandler;
import ru.ariadna.misca.toolbox.Toolbox;
import ru.ariadna.misca.tweaks.Tweaks;

import java.io.File;

@Mod(modid = "misca", version = "0.4.5")
public class Misca {
    public static File config_dir;
    static Logger logger = LogManager.getLogger("Misca");
    private DBHandler dbHandler = new DBHandler();
    private Tweaks tweaks = new Tweaks();
    private ChatChannels chatChannels = new ChatChannels();
    private Charsheets charsheets = new Charsheets();

    @EventHandler()
    public void preInit(FMLPreInitializationEvent event) {
        config_dir = new File(event.getModConfigurationDirectory(), "misca");
        config_dir.mkdirs();

        if (event.getSide().isServer()) {
            dbHandler.init();
        }

        tweaks.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        charsheets.init();
        tweaks.initCommon();

        if (event.getSide().isServer()) {
            tweaks.initServer();
        } else {
            tweaks.initClient();
        }
    }

    @EventHandler
    @SideOnly(Side.SERVER)
    public void serverStart(FMLServerStartingEvent event) {
        Toolbox.initServer(event);
        chatChannels.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        logger.info("Misca is fully loaded! Bon appetit!");
    }
}
