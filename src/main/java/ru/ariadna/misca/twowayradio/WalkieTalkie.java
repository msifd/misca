package ru.ariadna.misca.twowayradio;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class WalkieTalkie {
    public final SimpleNetworkWrapper network = new SimpleNetworkWrapper("misca.ht");
    public final FrequencyManager frequencyManager = new FrequencyManager();

    @Subscribe
    public void onInit(FMLInitializationEvent event) {
        ItemWalkieTalkie.register();
    }
}
