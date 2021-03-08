package msifeed.misca.rolls;

import msifeed.misca.Misca;
import msifeed.misca.charsheet.CharEffort;
import msifeed.sys.rpc.RpcContext;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RollRpc {
    private static final String skillRoll = "roll.effort";

    @RpcMethodHandler(skillRoll)
    public void onSkillRoll(RpcContext ctx, int entityId, int effortOrd, int mod) {
        final CharEffort effort = CharEffort.values()[effortOrd];
        System.out.println("roll " + effort.toString() + " mod " + mod);
    }

    @SideOnly(Side.CLIENT)
    public static void doEffortRoll(EntityLivingBase target, CharEffort effort, int mod) {
        Misca.RPC.sendToServer(skillRoll, target.getEntityId(), effort.ordinal(), mod);
    }
}
