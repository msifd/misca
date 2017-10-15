package msifeed.mc.misca.config;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.File;

public enum ConfigManager {
    INSTANCE;

    public static final EventBus eventbus = new EventBus();
    public static File config_dir;

    private SimpleNetworkWrapper network = new SimpleNetworkWrapper("misca.config");

    public void init(FMLPreInitializationEvent event) {
        config_dir = new File(event.getModConfigurationDirectory(), "misca");
        config_dir.mkdirs();

        network.registerMessage(ConfigSyncMessage.class, ConfigSyncMessage.class, 0, Side.CLIENT);
        FMLCommonHandler.instance().bus().register(INSTANCE);

        eventbus.post(new ConfigEvent.Reload());
        eventbus.post(new ConfigEvent.ReloadDone());
    }

    @SubscribeEvent
    @SideOnly(Side.SERVER)
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        network.sendTo(bakeSyncMessage(), (EntityPlayerMP) event.player);
    }

    public void reloadConfig() {
        eventbus.post(new ConfigEvent.Reload());
        network.sendToAll(bakeSyncMessage());
    }

    private ConfigSyncMessage bakeSyncMessage() {
        ConfigEvent.Sync event = new ConfigEvent.Sync();
        eventbus.post(event);

        ConfigSyncMessage msg = new ConfigSyncMessage();
        msg.configs = event.configs;
        return msg;
    }
}
