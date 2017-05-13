package ru.ariadna.misca;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ariadna.misca.blocks.MiscaBlocks;
import ru.ariadna.misca.channels.ChatChannels;
import ru.ariadna.misca.charsheet.Charsheets;
import ru.ariadna.misca.combat.Combat;
import ru.ariadna.misca.database.DBHandler;
import ru.ariadna.misca.gui.MiscaGui;
import ru.ariadna.misca.gui.MiscaKeyBinds;
import ru.ariadna.misca.toolbox.Toolbox;
import ru.ariadna.misca.tweaks.Tweaks;

import java.io.File;

@Mod(modid = "misca", version = "@VERSION@")
public class Misca {
    public static final String MODID = "misca";

    public static File config_dir;
    static Logger logger = LogManager.getLogger("Misca");

    @SidedProxy(clientSide = "ru.ariadna.misca.combat.CombatClient", serverSide = "ru.ariadna.misca.combat.Combat")
    private static Combat combat;

    @Mod.Instance
    private final Misca instance = this;
    private DBHandler dbHandler = new DBHandler();
    private Tweaks tweaks = new Tweaks();
    private ChatChannels chatChannels = new ChatChannels();
    private Charsheets charsheets = new Charsheets();

    private MiscaGui miscaGui = new MiscaGui();

    @EventHandler()
    public void preInit(FMLPreInitializationEvent event) {
        config_dir = new File(event.getModConfigurationDirectory(), "misca");
        config_dir.mkdirs();

        combat.preInit(event);
        tweaks.preInit();

        if (event.getSide().isServer()) {
            dbHandler.init();
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, miscaGui.guiHandler);

        MiscaBlocks.register();
        combat.init(event);
        charsheets.init();
        tweaks.initCommon();

        if (event.getSide().isServer()) {
            tweaks.initServer();
        } else {
            MiscaKeyBinds.register();
            tweaks.initClient();
        }
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        logger.info("Misca is fully loaded! Bon appetit!");
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        Toolbox.initServer(event);
        chatChannels.init(event);
        combat.serverStart(event);
    }
}
