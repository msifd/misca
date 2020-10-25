package msifeed.misca.combat.rpc;

import msifeed.sys.rpc.RpcMethodHandler;

public class CombatClientRpc implements ICombatRpc {
    @RpcMethodHandler(candidateRet)
    public void onCandidateRet(boolean state) {

    }
}
