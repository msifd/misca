package ru.ariadna.misca.crabs;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ariadna.misca.crabs.gui.CrabsGuiHandler;
import ru.ariadna.misca.crabs.lobby.LobbyManager;

public class Crabs {
    public static final Crabs instance = new Crabs();
    public static Logger logger = LogManager.getLogger("Misca-Crabs");

    public final SimpleNetworkWrapper network = new SimpleNetworkWrapper("misca.crabs");
    public final LobbyManager lobbyManager = new LobbyManager();

    private CrabsGuiHandler guiHandler = new CrabsGuiHandler();

    private Crabs() {

    }

    @Subscribe
    public void onPreInit(FMLPreInitializationEvent event) {
    }

    @Subscribe
    public void onInit(FMLInitializationEvent event) {
        lobbyManager.onInit();
        guiHandler.onInit();
    }
}
