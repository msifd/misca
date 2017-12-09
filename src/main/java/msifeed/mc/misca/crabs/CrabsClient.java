package msifeed.mc.misca.crabs;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import msifeed.mc.misca.crabs.client.BattleHud;
import msifeed.mc.misca.crabs.client.BattleMarkRender;
import msifeed.mc.misca.crabs.client.CrabsKeyBinds;
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
        MinecraftForge.EVENT_BUS.register(BattleMarkRender.INSTANCE);
        MinecraftForge.EVENT_BUS.register(BattleHud.INSTANCE);

        CrabsKeyBinds.register();
    }
}
