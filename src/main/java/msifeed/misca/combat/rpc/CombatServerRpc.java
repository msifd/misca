package msifeed.misca.combat.rpc;

import msifeed.misca.Misca;
import msifeed.misca.combat.battle.BattleManager;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


public class CombatServerRpc implements ICombatRpc {
    private final BattleManager battleManager;

    public CombatServerRpc(BattleManager battleManager) {
        this.battleManager = battleManager;
    }

    @RpcMethodHandler(candidate)
    public void onCandidate(MessageContext ctx) {
        final EntityPlayerMP sender = ctx.getServerHandler().player;
        battleManager.candidate(sender);
    }
}
