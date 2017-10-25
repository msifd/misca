package msifeed.mc.misca.config;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashMap;

public enum ConfigManager implements IMessageHandler<ConfigSyncMessage, ConfigSyncMessage> {
    INSTANCE;

    public static File config_dir;
    static Logger logger = LogManager.getLogger("Misca-Config");
    public final EventBus eventbus = new EventBus();
    private SimpleNetworkWrapper network = new SimpleNetworkWrapper("misca.config");
    private HashMap<String, JsonConfig> configHandlers = new HashMap<>();

    public static <T> JsonConfig<T> getConfigFor(Class<T> type, String filename) {
        JsonConfig<T> handler = new JsonConfig<>(type, filename);
        INSTANCE.eventbus.register(handler);
        INSTANCE.configHandlers.put(filename, handler);
        return handler;
    }

    public static <T> JsonConfig<T> getServerConfigFor(Class<T> type, String filename) {
        JsonConfig<T> handler = getConfigFor(type, filename);
        handler.setServerSided();
        return handler;
    }

    public void init(FMLPreInitializationEvent event) {
        config_dir = new File(event.getModConfigurationDirectory(), "misca");
        config_dir.mkdirs();

        network.registerMessage(INSTANCE, ConfigSyncMessage.class, 0, Side.CLIENT);
        FMLCommonHandler.instance().bus().register(INSTANCE);
    }

    @SubscribeEvent
    @SideOnly(Side.SERVER)
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        network.sendTo(collectOverrideMessage(), (EntityPlayerMP) event.player);
    }

    public void reloadConfig() {
        eventbus.post(new ConfigEvent.Reload());
        eventbus.post(new ConfigEvent.ReloadDone());
    }

    public void syncConfig() {
        network.sendToAll(collectOverrideMessage());
    }

    @Override
    public ConfigSyncMessage onMessage(ConfigSyncMessage message, MessageContext ctx) {
        ConfigEvent.Override event = new ConfigEvent.Override();
        event.configs = message.configs;
        eventbus.post(event);
        return null;
    }

    private ConfigSyncMessage collectOverrideMessage() {
        ConfigEvent.Collect event = new ConfigEvent.Collect();
        eventbus.post(event);

        ConfigSyncMessage msg = new ConfigSyncMessage();
        msg.configs = event.configs;
        return msg;
    }
}
