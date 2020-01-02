package msifeed.mc.misca.tweaks;

import com.google.common.net.InetAddresses;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum DRM {
    INSTANCE;

    private static final Logger LOG = LogManager.getLogger("DRM");
    private static final Set<InetAddress> SERVERS = Stream.of(
            "145.239.149.64",
            "51.77.71.251",
            "51.89.44.179",
            "51.89.9.222")
            .map(InetAddresses::forString)
            .collect(Collectors.toSet());

    public static void apply() {
        FMLCommonHandler.instance().bus().register(INSTANCE);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClientConnectedToServerEvent(ClientConnectedToServerEvent event) {
        final SocketAddress socket = event.manager.getSocketAddress();
        if (!(socket instanceof InetSocketAddress))
            return;

        final InetAddress address = ((InetSocketAddress) socket).getAddress();
        LOG.info("Connecting to {}", address.getHostName());
        if ( address.isLoopbackAddress() || address.isSiteLocalAddress() ||SERVERS.contains(address))
            return;

        LOG.info("Connection attempt rejected");
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI("https://www.youtube.com/watch?v=cQ_b4_lw0Gg"));
            } catch (Exception e) {
            }
        }

        // Также еще и крашит клиент. Мвахаха.
        event.manager.channel().disconnect();
    }
}
