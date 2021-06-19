package msifeed.misca.charstate.cap;

import msifeed.misca.Misca;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CharstateSync {
    private static final String sync = "charstate.sync";
    private static final String syncNonce = "charstate.nonce";

    public static void sync(EntityPlayerMP target) {
        final NBTTagCompound nbt = CharstateProvider.encode(CharstateProvider.get(target));
        Misca.RPC.sendTo(target, sync, nbt);
    }

    public static void syncNonce(EntityPlayerMP target) {
        Misca.RPC.sendTo(target, syncNonce, CharstateProvider.get(target).nonce());
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(sync)
    public void onSync(NBTTagCompound nbt) {
        update(Minecraft.getMinecraft().player, nbt);
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(syncNonce)
    public void onSyncNonce(int nonce) {
        CharstateProvider.get(Minecraft.getMinecraft().player).setNonce(nonce);
    }

    @SideOnly(Side.CLIENT)
    private void update(EntityPlayer target, NBTTagCompound nbt) {
        CharstateProvider.get(target).replaceWith(CharstateProvider.decode(nbt));
    }
}
