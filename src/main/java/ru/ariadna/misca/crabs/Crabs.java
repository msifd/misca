package ru.ariadna.misca.crabs;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ariadna.misca.charsheet.CharsheetMessage;
import ru.ariadna.misca.crabs.characters.CharacterMessage;
import ru.ariadna.misca.crabs.characters.CharacterProvider;
import ru.ariadna.misca.crabs.combat.CombatActionMessage;
import ru.ariadna.misca.crabs.combat.CombatUpdateMessage;
import ru.ariadna.misca.crabs.combat.FightManager;
import ru.ariadna.misca.crabs.gui.CrabsGuiHandler;
import ru.ariadna.misca.crabs.gui.CrabsKeyHandler;
import ru.ariadna.misca.crabs.lobby.LobbyActionMessage;
import ru.ariadna.misca.crabs.lobby.LobbyManager;
import ru.ariadna.misca.crabs.lobby.LobbyUpdateMessage;

public class Crabs {
    public static final Crabs instance = new Crabs();
    public static Logger logger = LogManager.getLogger("Misca-Crabs");

    public final SimpleNetworkWrapper network = new SimpleNetworkWrapper("misca.crabs");
    public final CharacterProvider characterProvider = new CharacterProvider();
    public final LobbyManager lobbyManager = new LobbyManager();
    public final FightManager fightManager = new FightManager();

    private CrabsGuiHandler guiHandler = new CrabsGuiHandler();
    private CrabsKeyHandler keyHandler = new CrabsKeyHandler();

    @Subscribe
    public void onPreInit(FMLPreInitializationEvent event) {
    }

    @Subscribe
    public void onInit(FMLInitializationEvent event) {
        characterProvider.onInit();
        lobbyManager.onInit();
        fightManager.onInit();

        if (event.getSide().isClient()) {
            guiHandler.onInit();
            keyHandler.onInit();
        }

        network.registerMessage(LobbyActionMessage.class, LobbyActionMessage.class, 0, Side.SERVER);
        network.registerMessage(LobbyUpdateMessage.class, LobbyUpdateMessage.class, 1, Side.CLIENT);
        network.registerMessage(CombatActionMessage.class, CombatActionMessage.class, 2, Side.SERVER);
        network.registerMessage(CombatUpdateMessage.class, CombatUpdateMessage.class, 3, Side.CLIENT);
        network.registerMessage(CharacterMessage.class, CharacterMessage.class, 4, Side.SERVER);
        network.registerMessage(CharacterMessage.class, CharacterMessage.class, 5, Side.CLIENT);
    }
}
