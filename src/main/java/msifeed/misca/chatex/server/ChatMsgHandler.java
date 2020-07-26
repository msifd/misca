package msifeed.misca.chatex.server;

import msifeed.misca.chatex.IChatexRpc;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatMsgHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onChatMessage(ServerChatEvent event) {
        IChatexRpc.sendSpeech(event.getPlayer(), event.getComponent());
        event.setCanceled(true);
    }
}
