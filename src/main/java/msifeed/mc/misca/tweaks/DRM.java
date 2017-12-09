package msifeed.mc.misca.tweaks;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

import java.awt.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;

public class DRM {
    @SubscribeEvent
    public void onClientConnectedToServerEvent(ClientConnectedToServerEvent event) {
        SocketAddress address = event.manager.getSocketAddress();
        if (!(address instanceof InetSocketAddress)) return;

        String host = ((InetSocketAddress) address).getHostString();
        if (host.equals("localhost") || host.equals("ariadna-rp.ru")) return;

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
