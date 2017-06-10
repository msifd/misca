package ru.ariadna.misca.tweaks;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Tweaks {
    static final TweaksConfig config = new TweaksConfig();
    static Logger logger = LogManager.getLogger("Misca-Tweaks");

    private OfftopFormat offtopFormat = new OfftopFormat();
    private HideNametag hideNametag = new HideNametag();
    private SpawnInvincibility spawnInvincibility = new SpawnInvincibility();
    private DisableSomeCraftingTables disableSomeCraftingTables = new DisableSomeCraftingTables();

    @Subscribe
    public void onPreInit(FMLPreInitializationEvent event) {
        config.load();
    }

    @Subscribe
    public void onInit(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(disableSomeCraftingTables);

        if (event.getSide().isServer()) {
            MinecraftForge.EVENT_BUS.register(offtopFormat);
            MinecraftForge.EVENT_BUS.register(spawnInvincibility);
        } else {
            MinecraftForge.EVENT_BUS.register(hideNametag);
        }
    }
}
