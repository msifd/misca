package msifeed.misca.combat.rpc;

import msifeed.misca.combat.battle.BattleManager;
import msifeed.sys.rpc.RpcContext;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.entity.player.EntityPlayerMP;


public class CombatServerRpc implements ICombatRpc {
    private final BattleManager battleManager;

    public CombatServerRpc(BattleManager battleManager) {
        this.battleManager = battleManager;
    }

    @RpcMethodHandler(candidate)
    public void onCandidate(RpcContext ctx) {
        final EntityPlayerMP sender = ctx.getServerHandler().player;
        battleManager.candidate(sender);
    }
}
