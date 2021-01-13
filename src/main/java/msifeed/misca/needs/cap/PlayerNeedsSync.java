package msifeed.misca.needs.cap;

import msifeed.misca.Misca;
import msifeed.sys.rpc.RpcContext;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PlayerNeedsSync {
    private static final String stamina = "needs.sta";

    public static void sync(EntityPlayerMP target) {
        final IPlayerNeeds needs = PlayerNeedsProvider.get(target);

        if (needs.get(IPlayerNeeds.NeedType.stamina) != IPlayerNeeds.NeedType.stamina.max)
            Misca.RPC.sendTo(target, stamina, needs.get(IPlayerNeeds.NeedType.stamina));
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(stamina)
    public void onStamina(RpcContext ctx, float value) {
        final IPlayerNeeds needs = PlayerNeedsProvider.get(Minecraft.getMinecraft().player);
        needs.set(IPlayerNeeds.NeedType.stamina, value);
    }
}
