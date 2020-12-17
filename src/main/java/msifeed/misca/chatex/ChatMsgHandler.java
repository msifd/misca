package msifeed.misca.chatex;

import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatMsgHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onChatMessage(ServerChatEvent event) {
        final int range = ChatexUtils.getSpeechRange(event.getMessage());
        ChatexRpc.broadcastSpeech(event.getPlayer(), range, event.getMessage());
        event.setCanceled(true);
    }
}
