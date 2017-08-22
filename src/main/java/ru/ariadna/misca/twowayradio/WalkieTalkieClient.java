package ru.ariadna.misca.twowayradio;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

public class WalkieTalkieClient extends WalkieTalkie {
    WalkieTalkieAdjustmentHud hudRender = new WalkieTalkieAdjustmentHud();

    @Subscribe
    public void onInit(FMLInitializationEvent event) {
        super.onInit(event);

        MinecraftForge.EVENT_BUS.register(hudRender);
    }
}
