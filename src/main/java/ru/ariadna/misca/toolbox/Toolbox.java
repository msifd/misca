package ru.ariadna.misca.toolbox;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Toolbox {
    static Logger logger = LogManager.getLogger("Misca-Toolbox");
    private CommandBiomeEnchantIds commandIds = new CommandBiomeEnchantIds();

    @Subscribe
    public void onServerStart(FMLServerStartingEvent event) {
        event.registerServerCommand(commandIds);
    }
}
