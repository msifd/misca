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
import ru.ariadna.misca.chat.ChatFormat;
import ru.ariadna.misca.client.HideNametag;

@Mod(modid = "misca", version = "0.3.2")
public class Misca {
    public static Logger logger = LogManager.getLogger("Misca");

    public static MiscaConfig config;

    private ChatFormat chatFormat = new ChatFormat();
    private ChatChannels chatChannels = new ChatChannels();
    private HideNametag hideNametag = new HideNametag();
    private Charsheets charsheets = new Charsheets();

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
        MinecraftForge.EVENT_BUS.register(chatFormat);
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
