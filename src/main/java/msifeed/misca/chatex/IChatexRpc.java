package msifeed.misca.chatex;

import msifeed.misca.Misca;
import net.minecraft.entity.player.EntityPlayerMP;

public interface IChatexRpc {
    String speech = "chatex.speech";
    String global = "chatex.global";
    String roll = "chatex.roll";

    String notifyTyping = "chatex.typing.notify";
    String broadcastTyping = "chatex.typing.broadcast";

    static void sendSpeech(EntityPlayerMP player, int range, String msg) {
        Misca.RPC.sendToAllTracking(player, speech, player.getUniqueID(), range, msg);
        Misca.RPC.sendTo(player, speech, player.getUniqueID(), range, msg);
    }

    static void sendGlobal(EntityPlayerMP player, String msg) {
        // TODO: get name from charsheet
        Misca.RPC.sendToAll(global, player.getDisplayName(), msg);
    }

    static void sendRoll(EntityPlayerMP sender, String spec, long result) {
        Misca.RPC.sendToAllTracking(sender, roll, sender.getUniqueID(), spec, result);
        Misca.RPC.sendTo(sender, roll, sender.getUniqueID(), spec, result);
    }

    // // // //

    static void notifyTyping() {
        Misca.RPC.sendToServer(notifyTyping);
    }
}
