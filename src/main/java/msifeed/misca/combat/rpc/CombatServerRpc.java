package msifeed.misca.combat.rpc;

import msifeed.misca.Misca;
import msifeed.misca.combat.battle.BattleManager;
import msifeed.sys.rpc.RpcContext;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.UUID;


public class CombatServerRpc implements ICombatRpc {
    private final BattleManager battleManager;

    public CombatServerRpc(BattleManager battleManager) {
        this.battleManager = battleManager;
    }

    @RpcMethodHandler(invite)
    public void onInvite(RpcContext ctx, UUID target) {
        final EntityPlayerMP sender = ctx.getServerHandler().player;
        final EntityPlayerMP player = (EntityPlayerMP) sender.world.getPlayerEntityByUUID(target);
        Misca.RPC.sendTo(player, invite, sender.getUniqueID());
    }

    @RpcMethodHandler(acceptInvite)
    public void onAcceptInvite(RpcContext ctx, UUID initiatorUuid) {
        final EntityPlayerMP sender = ctx.getServerHandler().player;
        final EntityPlayerMP initiator = (EntityPlayerMP) sender.world.getPlayerEntityByUUID(initiatorUuid);
    }
}
