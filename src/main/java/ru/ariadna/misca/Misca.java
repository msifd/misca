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
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ariadna.misca.channels.ChatChannels;
import ru.ariadna.misca.charsheet.Charsheets;
import ru.ariadna.misca.chat.OfftopFormat;
import ru.ariadna.misca.client.HideNametag;
import ru.ariadna.misca.combat.Combat;
import ru.ariadna.misca.database.DBHandler;
import ru.ariadna.misca.gui.MiscaGui;
import ru.ariadna.misca.gui.MiscaKeyBinds;

import java.io.File;

@Mod(modid = "misca", version = "0.5")
public class Misca {
    public static File config_dir;
    static Logger logger = LogManager.getLogger("Misca");

    @SidedProxy(clientSide = "ru.ariadna.misca.combat.CombatClient", serverSide = "ru.ariadna.misca.combat.Combat")
    private static Combat combat;

    @Mod.Instance
    private final Misca instance = this;
    private DBHandler dbHandler = new DBHandler();
    private OfftopFormat offtopFormat = new OfftopFormat();
    private ChatChannels chatChannels = new ChatChannels();
    private HideNametag hideNametag = new HideNametag();
    private Charsheets charsheets = new Charsheets();


    private MiscaGui miscaGui = new MiscaGui();

    @EventHandler()
    public void preInit(FMLPreInitializationEvent event) {
        config_dir = new File(event.getModConfigurationDirectory(), "misca");
        config_dir.mkdirs();

        combat.preInit(event);

        if (event.getSide().isServer()) {
            dbHandler.init();
        }
    }

    @EventHandler
    public void initCommon(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, miscaGui.guiHandler);
        combat.init(event);
        charsheets.init();
    }

    @EventHandler
    @SideOnly(Side.SERVER)
    public void initServer(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(offtopFormat);
    }

    @EventHandler
    @SideOnly(Side.CLIENT)
    public void initClient(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(hideNametag);
        MiscaKeyBinds.register();
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        chatChannels.init(event);
        combat.serverStart(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        logger.info("Misca is fully loaded! Bon appetit!");
    }
}
