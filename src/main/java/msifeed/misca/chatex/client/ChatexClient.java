package msifeed.misca.chatex.client;

import msifeed.misca.Misca;
import net.minecraftforge.common.MinecraftForge;

public enum ChatexClient {
    INSTANCE;

    public void init() {
        Misca.RPC.register(new ChatexClientRpc());
        MinecraftForge.EVENT_BUS.register(new TypingGuiHandler());
        MinecraftForge.EVENT_BUS.register(new NametagHandler());
    }
}
