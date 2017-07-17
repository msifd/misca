package ru.ariadna.misca.crabs;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import ru.ariadna.misca.crabs.characters.CharacterMessage;
import ru.ariadna.misca.crabs.combat.CombatUpdateMessage;
import ru.ariadna.misca.crabs.gui.CrabsGuiHandler;
import ru.ariadna.misca.crabs.gui.CrabsKeyHandler;
import ru.ariadna.misca.crabs.lobby.LobbyUpdateMessage;

public class CrabsClient extends Crabs {
    private CrabsGuiHandler guiHandler = new CrabsGuiHandler();
    private CrabsKeyHandler keyHandler = new CrabsKeyHandler();

    @Override
    public void onInit(FMLInitializationEvent event) {
        guiHandler.onInit();
        keyHandler.onInit();

        super.onInit(event);

        network.registerMessage(LobbyUpdateMessage.class, LobbyUpdateMessage.class, 1, Side.CLIENT);
        network.registerMessage(CombatUpdateMessage.class, CombatUpdateMessage.class, 3, Side.CLIENT);
        network.registerMessage(CharacterMessage.class, CharacterMessage.class, 5, Side.CLIENT);
    }
}
