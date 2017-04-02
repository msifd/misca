package ru.ariadna.misca;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ariadna.misca.channels.ChatChannels;
import ru.ariadna.misca.charsheet.Charsheets;
import ru.ariadna.misca.chat.OfftopFormat;
import ru.ariadna.misca.client.HideNametag;

@Mod(modid = "misca", version = "0.4")
public class Misca {
    static Logger logger = LogManager.getLogger("Misca");

    public static MiscaConfig config;

    private OfftopFormat offtopFormat = new OfftopFormat();
    private HideNametag hideNametag = new HideNametag();
    private Charsheets charsheets = new Charsheets();
    private ChatChannels chatChannels = new ChatChannels();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config = new MiscaConfig(event.getModConfigurationDirectory());
    }

    @EventHandler
    public void initCommon(FMLInitializationEvent event) {
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
    }

    @EventHandler
    @SideOnly(Side.SERVER)
    public void serverStart(FMLServerStartingEvent event) {
        chatChannels.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        logger.info("Misca is fully loaded! Bon appetit!");
    }
}
