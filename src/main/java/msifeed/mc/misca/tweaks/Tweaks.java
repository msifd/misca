package msifeed.mc.misca.tweaks;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Tweaks {
    public static Logger logger = LogManager.getLogger("Misca-Tweaks");
    public final SimpleNetworkWrapper network = new SimpleNetworkWrapper("misca.tweaks");

    private OfftopFormat offtopFormat = new OfftopFormat();
    private HideNametag hideNametag = new HideNametag();
    private SpawnInvincibility spawnInvincibility = new SpawnInvincibility();
    private DisableSomeCraftingTables disableSomeCraftingTables = new DisableSomeCraftingTables();
    private DeathToll deathToll = new DeathToll();
    private HealthNotification healthNotification = new HealthNotification();
    private BanEntities banEntities = new BanEntities();
    private FoodSupplier foodSupplier = new FoodSupplier();
    private DRM drm = new DRM();

    @Subscribe
    public void onInit(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(disableSomeCraftingTables);
        MinecraftForge.EVENT_BUS.register(deathToll);
        MinecraftForge.EVENT_BUS.register(healthNotification);
        MinecraftForge.EVENT_BUS.register(banEntities);
        MinecraftForge.EVENT_BUS.register(foodSupplier);
        FMLCommonHandler.instance().bus().register(drm);

        if (event.getSide().isServer()) {
            MinecraftForge.EVENT_BUS.register(offtopFormat);
            MinecraftForge.EVENT_BUS.register(spawnInvincibility);
        } else {
            MinecraftForge.EVENT_BUS.register(hideNametag);
        }

        network.registerMessage(hideNametag, HideNametag.MessageIncognito.class, 1, Side.CLIENT);
    }

    @Subscribe
    public void onServerStart(FMLServerStartingEvent event) {
        event.registerServerCommand(hideNametag.commandIncognito);
    }
}
