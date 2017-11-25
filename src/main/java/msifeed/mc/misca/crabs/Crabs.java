package msifeed.mc.misca.crabs;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import msifeed.mc.misca.crabs.fight.FightNetman;

public class Crabs {
    @Subscribe
    public void preInit(FMLPreInitializationEvent event) {
        FightNetman.INSTANCE.init(event);
    }
}
