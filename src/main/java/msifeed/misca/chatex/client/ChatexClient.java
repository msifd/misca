package msifeed.misca.chatex.client;

import msifeed.misca.Misca;
import msifeed.misca.chatex.ChatexServer;

public class ChatexClient {
    public void init() {
        Misca.RPC.register(new ChatexClientRpc());
        Misca.RPC.register(new ChatexClientRpc());
    }
}
