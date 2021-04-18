package msifeed.misca.charstate.cap;

import msifeed.misca.Misca;
import msifeed.sys.rpc.RpcMethodHandler;
import msifeed.sys.rpc.RpcUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CharstateSync {
    private static final String sync = "charstate.sync";

    public static void sync(EntityPlayer target) {
        final NBTTagCompound nbt = CharstateProvider.encode(CharstateProvider.get(target));
        Misca.RPC.sendToAllAround(target, sync, target.getEntityId(), nbt);
    }

    public static void sync(EntityPlayerMP receiver, EntityPlayer target) {
        final NBTTagCompound nbt = CharstateProvider.encode(CharstateProvider.get(target));
        Misca.RPC.sendTo(receiver, sync, target.getEntityId(), nbt);
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(sync)
    public void onSync(int eid, NBTTagCompound nbt) {
        final EntityPlayer target = RpcUtils.findPlayer(Minecraft.getMinecraft().world, eid);
        if (target != null)
            update(target, nbt);
    }

    @SideOnly(Side.CLIENT)
    private void update(EntityPlayer target, NBTTagCompound nbt) {
        CharstateProvider.get(target).replaceWith(CharstateProvider.decode(nbt));
    }
}
