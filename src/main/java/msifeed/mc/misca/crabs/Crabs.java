package msifeed.mc.misca.crabs;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import msifeed.mc.misca.crabs.battle.BattleManager;

public class Crabs {
    @Subscribe
    public void preInit(FMLPreInitializationEvent event) {
    }

    @Subscribe
    public void init(FMLInitializationEvent event) {
        BattleManager.INSTANCE.onInit(event);
    }
}
