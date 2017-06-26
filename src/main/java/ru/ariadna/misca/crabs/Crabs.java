package ru.ariadna.misca.crabs;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ariadna.misca.crabs.lobby.LobbyManager;

public class Crabs {
    private static final Crabs instance = new Crabs();
    public static Logger logger = LogManager.getLogger("Misca-Crabs");
    public final LobbyManager lobbyManager = new LobbyManager();

    private Crabs() {
    }

    public static Crabs instance() {
        return instance;
    }

    @Subscribe
    public void onPreInit(FMLPreInitializationEvent event) {
    }

    @Subscribe
    public void onInit(FMLInitializationEvent event) {
        lobbyManager.onInit(event);
    }
}
