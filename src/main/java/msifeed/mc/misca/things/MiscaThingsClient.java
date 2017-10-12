package msifeed.mc.misca.things;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import msifeed.mc.misca.things.client.RegularChestEntityRenderer;

public class MiscaThingsClient extends MiscaThings {
    @Subscribe
    public void onInit(FMLInitializationEvent event) {
        super.onInit(event);

        ClientRegistry.bindTileEntitySpecialRenderer(RegularChest.ChestEntity.class, new RegularChestEntityRenderer());
    }
}
