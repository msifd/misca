package ru.ariadna.misca.toolbox;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Toolbox {
    static Logger logger = LogManager.getLogger("Misca-Toolbox");
    private static CommandBiomeEnchantIds commandIds = new CommandBiomeEnchantIds();

    public static void initServer(FMLServerStartingEvent event) {
        event.registerServerCommand(commandIds);
    }
}
