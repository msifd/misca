package msifeed.misca.combat.rpc;

import msifeed.misca.Misca;

public interface ICombatRpc {
    String candidate = "combat.candidate";
    String candidateRet = "combat.candidate.ret";

    static void candidate() {
        Misca.RPC.sendToServer(candidate);
    }
}
