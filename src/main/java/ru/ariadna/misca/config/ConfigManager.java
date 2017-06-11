package ru.ariadna.misca.config;

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
import ru.ariadna.misca.Misca;

import java.io.File;

public class ConfigManager implements IMessageHandler<ConfigSyncMessage, ConfigSyncMessage> {
    public static final ConfigManager instance = new ConfigManager();

    public static File config_dir;
    private SimpleNetworkWrapper network = new SimpleNetworkWrapper("misca.config");

    private ConfigManager() {
    }

    public void initConfig(FMLPreInitializationEvent event) {
        config_dir = new File(event.getModConfigurationDirectory(), "misca");
        config_dir.mkdirs();

        network.registerMessage(this, ConfigSyncMessage.class, 0, Side.CLIENT);
        FMLCommonHandler.instance().bus().register(this);

        Misca.eventBus().post(new ConfigReloadEvent());
    }

    @SubscribeEvent
    @SideOnly(Side.SERVER)
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        network.sendTo(bakeSyncMessage(), (EntityPlayerMP) event.player);
    }

    @Override
    public ConfigSyncMessage onMessage(ConfigSyncMessage message, MessageContext ctx) {
        ConfigSyncEvent event = new ConfigSyncEvent();
        event.configs = message.configs;
        Misca.eventBus().post(event);

        return null;
    }

    public void reloadConfig() {
        Misca.eventBus().post(new ConfigReloadEvent());
        network.sendToAll(bakeSyncMessage());
    }

    private ConfigSyncMessage bakeSyncMessage() {
        ConfigSyncEvent event = new ConfigSyncEvent();
        Misca.eventBus().post(event);

        ConfigSyncMessage msg = new ConfigSyncMessage();
        msg.configs = event.configs;
        return msg;
    }
}
