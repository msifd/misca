package msifeed.mc.misca.tweaks;

import com.google.common.net.InetAddresses;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

import java.awt.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;

public class DRM {
    private static final InetAddress LOCALHOST = InetAddresses.forString("127.0.0.1");
    private static final InetAddress ARIADNA = InetAddresses.forString("46.105.37.9");

    @SubscribeEvent
    public void onClientConnectedToServerEvent(ClientConnectedToServerEvent event) {
        final SocketAddress socket = event.manager.getSocketAddress();
        if (!(socket instanceof InetSocketAddress)) return;

        final InetAddress address = ((InetSocketAddress) socket).getAddress();
        if (address.equals(LOCALHOST) || address.equals(ARIADNA)) return;

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
