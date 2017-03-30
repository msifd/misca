package ru.ariadna.misca;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ariadna.misca.chat.ChatFormat;
import ru.ariadna.misca.client.HideNametag;

@Mod(modid = "misca", name = "Misca", version = "0.2", acceptableRemoteVersions = "*")
public class Misca {
    private static Logger logger = LogManager.getLogger("Misca");

    private ChatFormat chatFormat = new ChatFormat();
    private HideNametag hideNametag = new HideNametag();

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(chatFormat);
        MinecraftForge.EVENT_BUS.register(hideNametag);

        logger.info("Misca is fully loaded! Bon appetit!");
    }
}
