package msifeed.misca.logdb;

import msifeed.misca.MiscaConfig;
import msifeed.misca.keeper.KeeperConfig;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum LogDB {
    INSTANCE;

    static final Logger LOG = LogManager.getLogger("Misca-LogDB");

    private LogDBConnector connector;

    public static void reload() {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) return;
        if (MiscaConfig.logDb.disabled) return;

        MinecraftForge.EVENT_BUS.register(INSTANCE);

        try {
            LOG.info("Try to connect to LogDB...");
            INSTANCE.connector = new LogDBConnector();
            LOG.info("Connection to LogDB is successful");
        } catch (Exception e) {
            LOG.error("Failed to connect to LogDB", e);
        }
    }

    public void log(ICommandSender sender, String type, ITextComponent tc) {
        log(sender, type, tc.getUnformattedText());
    }

    public void log(ICommandSender sender, String type, String text) {
        if (connector == null) return;
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) return;

        connector.log(sender, type, text);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        log(event.player, "log", "[logged in]");
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        log(event.player, "log", "[logged out]");
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntityLiving().world.isRemote) return;
        if (!(event.getEntityLiving() instanceof EntityPlayer)) return;

        final String msg;
        if (event.getSource() instanceof EntityDamageSource) {
            final EntityDamageSource src = (EntityDamageSource) event.getSource();
            msg = String.format("[death] source: %s, entity: %s, immediate entity: %s",
                    src.damageType,
                    src.getTrueSource() != null ? src.getTrueSource().getName() : "???",
                    src.getImmediateSource() != null ? src.getImmediateSource().getName() : "???");
        } else {
            msg = String.format("[death] source: %s", event.getSource().damageType);
        }

        log(event.getEntityLiving(), "log", msg);
    }

    @SubscribeEvent
    public void onCommand(CommandEvent event) {
        final String cmd = "/" + event.getCommand().getName() + " " + String.join(" ", event.getParameters());
        log(event.getSender(), "cmd", cmd);
    }
}
