package msifeed.mc.misca.crabs;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import msifeed.mc.misca.crabs.client.BattleMarkRender;
import msifeed.mc.misca.crabs.client.CrabsKeyBinds;
import msifeed.mc.misca.crabs.client.hud.HudManager;
import net.minecraftforge.common.MinecraftForge;

public class CrabsClient extends Crabs {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        CrabsKeyBinds.register();
        HudManager.INSTANCE.init();
        MinecraftForge.EVENT_BUS.register(BattleMarkRender.INSTANCE);
    }
}
