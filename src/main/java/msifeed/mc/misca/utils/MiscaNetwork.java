package msifeed.mc.misca.utils;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import msifeed.mc.misca.books.MessageRemoteBook;
import net.minecraft.entity.player.EntityPlayerMP;

public enum MiscaNetwork {
    INSTANCE;

    private SimpleNetworkWrapper network = new SimpleNetworkWrapper("misca");

    public void onInit() {
        network.registerMessage(MessageRemoteBook.class, MessageRemoteBook.class, 0x00, Side.SERVER);
        network.registerMessage(MessageRemoteBook.class, MessageRemoteBook.class, 0x01, Side.CLIENT);
    }

    public void sendToPlayer(EntityPlayerMP playerMP, IMessage message) {
        network.sendTo(message, playerMP);
    }

    public void sendToAll(IMessage message) {
        network.sendToAll(message);
    }

    public void sendToServer(IMessage message) {
        network.sendToServer(message);
    }
}
