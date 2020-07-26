package msifeed.misca.chatex.client;

import msifeed.misca.chatex.IChatexRpc;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;

public class TypingHandler {
    private static final int TYPING_PING_MS = 3000;

    private static final HashMap<Integer, Long> players = new HashMap<>();
    private static long lastNotify = 0;

    public static boolean isTyping(EntityPlayer player) {
        final Long typingStarted = players.get(player.getEntityId());
        if (typingStarted == null)
            return false;
        return System.currentTimeMillis() - typingStarted > TYPING_PING_MS;
    }

    public static void notifyTyping() {
        final long now = System.currentTimeMillis();
        if (now - lastNotify > TYPING_PING_MS / 2) {
            lastNotify = now;
            IChatexRpc.notifyTyping();
        }
    }

    public static void updateTyping(int entityId, long time) {
        players.put(entityId, time);
    }
}
