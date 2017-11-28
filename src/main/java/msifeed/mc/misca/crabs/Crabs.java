package msifeed.mc.misca.crabs;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import msifeed.mc.misca.crabs.client.CrabsRenderHandler;
import msifeed.mc.misca.crabs.fight.FightNetman;
import net.minecraftforge.common.MinecraftForge;

public class Crabs {
    @Subscribe
    public void preInit(FMLPreInitializationEvent event) {
//        FightNetman.INSTANCE.init(event);
    }

    @Subscribe
    public void init(FMLInitializationEvent event) {
    }
}
