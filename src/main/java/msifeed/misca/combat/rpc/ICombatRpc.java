package msifeed.misca.combat.rpc;

import msifeed.misca.Misca;

import java.util.UUID;

public interface ICombatRpc {
    String invite = "combat.invite";
    String acceptInvite = "combat.acceptInvite";

    static void invite(UUID target) {
        Misca.RPC.sendToServer(invite, target);
    }
    static void acceptInvite(UUID initiator) {
        Misca.RPC.sendToServer(acceptInvite, initiator);
    }
}
