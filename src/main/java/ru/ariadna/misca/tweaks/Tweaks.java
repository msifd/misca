package ru.ariadna.misca.tweaks;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Tweaks {
    static Logger logger = LogManager.getLogger("Misca-Tweaks");

    private OfftopFormat offtopFormat = new OfftopFormat();
    private HideNametag hideNametag = new HideNametag();
    private SpawnInvincibility spawnInvincibility = new SpawnInvincibility();
    private DisableSomeCraftingTables disableSomeCraftingTables = new DisableSomeCraftingTables();
    private DeathToll deathToll = new DeathToll();
    private HealthNotification healthNotification = new HealthNotification();

    @Subscribe
    public void onInit(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(disableSomeCraftingTables);

        MinecraftForge.EVENT_BUS.register(deathToll);
        MinecraftForge.EVENT_BUS.register(healthNotification);
        if (event.getSide().isServer()) {
            MinecraftForge.EVENT_BUS.register(offtopFormat);
            MinecraftForge.EVENT_BUS.register(spawnInvincibility);
        } else {
            MinecraftForge.EVENT_BUS.register(hideNametag);
        }
    }
}
