package msifeed.misca.charstate.cap;

import msifeed.misca.Misca;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class CharstateSync {
    private static final String sync = "charstate.sync";
    private static final String syncSelf = "charstate.syncSelf";

    public static void sync(EntityPlayer target) {
        final NBTTagCompound nbt = CharstateProvider.encode(CharstateProvider.get(target));
        Misca.RPC.sendToAllVisibleTo(target, sync, target.getUniqueID(), nbt);
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(syncSelf)
    public void onSyncSelf(NBTTagCompound nbt) {
        final EntityPlayer self = Minecraft.getMinecraft().player;
        update(self, nbt);
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(sync)
    public void onSync(UUID uuid, NBTTagCompound nbt) {
        final EntityPlayer target = Minecraft.getMinecraft().world.getPlayerEntityByUUID(uuid);
        if (target != null)
            update(target, nbt);
    }

    @SideOnly(Side.CLIENT)
    private void update(EntityPlayer target, NBTTagCompound nbt) {
        CharstateProvider.get(target).replaceWith(CharstateProvider.decode(nbt));
    }
}
