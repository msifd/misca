package msifeed.misca.rolls;

import msifeed.misca.Misca;
import msifeed.misca.charsheet.CharEffort;
import msifeed.misca.charstate.cap.CharstateProvider;
import msifeed.misca.charstate.cap.CharstateSync;
import msifeed.misca.charstate.cap.ICharstate;
import msifeed.misca.chatex.ChatexRpc;
import msifeed.sys.rpc.RpcContext;
import msifeed.sys.rpc.RpcMethodHandler;
import msifeed.sys.rpc.RpcUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RollRpc {
    private static final String effortRoll = "roll.effort";

    @RpcMethodHandler(effortRoll)
    public void onEffortRoll(RpcContext ctx, int eid, int effortOrd, int amount, int difficulty) {
        final EntityPlayerMP sender = ctx.getServerHandler().player;
        final EntityPlayerMP target = RpcUtils.findPlayerMP(sender.world, eid);

        final CharEffort effort = CharEffort.values()[effortOrd];
        final ICharstate state = CharstateProvider.get(sender);

        final float available = state.efforts().get(effort);
        if (amount > available) {
            sender.sendStatusMessage(new TextComponentString("Not enough efforts in pool!"), true);
            return;
        }

        state.efforts().set(effort, available - amount);
        CharstateSync.sync(target);

        final double chance = amount / (double) difficulty;
        final boolean success = Dices.check(chance);
        ChatexRpc.broadcastEffortRoll(target, effort, amount, difficulty, success);
    }

    @SideOnly(Side.CLIENT)
    public static void doEffortRoll(EntityLivingBase target, CharEffort effort, int amount, int difficulty) {
        Misca.RPC.sendToServer(effortRoll, target.getEntityId(), effort.ordinal(), amount, difficulty);
    }
}
