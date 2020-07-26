package msifeed.misca.chatex;

import msifeed.misca.Misca;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;

import java.util.UUID;

public interface IChatexRpc {
    String speech = "chat.speech";
    String global = "chat.global";
    String roll = "chat.roll";

    String notifyTyping = "chat.typing.notify";
    String broadcastTyping = "chat.typing.broadcast";

    static void sendSpeech(EntityPlayerMP player, ITextComponent msg) {
        Misca.RPC.sendToAllTracking(player, speech, player.getEntityId(), msg);
        Misca.RPC.sendTo(player, speech, player.getEntityId(), msg);
    }

    static void sendGlobal(EntityPlayerMP player, ITextComponent msg) {
        Misca.RPC.sendToAllTracking(player, global, player.getEntityId(), msg);
        Misca.RPC.sendTo(player, global, player.getEntityId(), msg);
    }

    static void sendRoll(EntityPlayerMP sender, String spec, long result) {
        Misca.RPC.sendToAllTracking(sender, roll, sender.getUniqueID(), spec, result);
        Misca.RPC.sendTo(sender, roll, sender.getUniqueID(), spec, result);
    }

    // // // //

    static void notifyTyping() {
        Misca.RPC.sendToServer(notifyTyping);
    }

    static void broadcastTyping(EntityPlayerMP player) {
        final long now = System.currentTimeMillis();
        Misca.RPC.sendToAllTracking(player, broadcastTyping, player.getEntityId(), now);
    }
}
