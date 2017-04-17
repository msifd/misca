package ru.ariadna.misca.combat;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventBus;
import ru.ariadna.misca.combat.gui.CombatScreen;

public class CombatClient extends Combat {
    private CombatScreen combatScreen = new CombatScreen();

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        EventBus bus = FMLCommonHandler.instance().bus();
        bus.register(combatScreen);
    }
}
