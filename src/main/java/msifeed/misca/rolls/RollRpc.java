package msifeed.misca.rolls;

import msifeed.misca.Misca;
import msifeed.misca.charsheet.CharEffort;
import msifeed.misca.chatex.ChatexRpc;
import msifeed.sys.rpc.RpcContext;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RollRpc {
    private static final String skillRoll = "roll.effort";

    @RpcMethodHandler(skillRoll)
    public void onSkillRoll(RpcContext ctx, int entityId, int effortOrd, int mod) {
        final EntityPlayerMP sender = ctx.getServerHandler().player;
        final CharEffort effort = CharEffort.values()[effortOrd];

        ChatexRpc.broadcastRoll(sender, "effort " + effort.toString() + " mod " + mod, mod);
    }

    @SideOnly(Side.CLIENT)
    public static void doEffortRoll(EntityLivingBase target, CharEffort effort, int mod) {
        Misca.RPC.sendToServer(skillRoll, target.getEntityId(), effort.ordinal(), mod);
    }
}
