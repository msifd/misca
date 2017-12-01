package msifeed.mc.misca.crabs;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import msifeed.mc.misca.crabs.client.CrabsRenderHandler;
import net.minecraftforge.common.MinecraftForge;

public class CrabsClient extends Crabs {
    @Subscribe
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Subscribe
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        MinecraftForge.EVENT_BUS.register(CrabsRenderHandler.INSTANCE);
    }
}
