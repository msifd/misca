package msifeed.misca.chatex.client;

import msifeed.misca.Misca;
import msifeed.misca.chatex.server.ChatexServer;

public class ChatexClient extends ChatexServer {
    @Override
    public void init() {
        super.init();
        Misca.RPC.register(new ChatexClientRpc());
    }
}
