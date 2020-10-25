package msifeed.misca.charsheet.client;

import msifeed.misca.Misca;
import msifeed.misca.charsheet.ICharsheetRpc;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.charsheet.cap.ICharsheet;
import msifeed.sys.rpc.RpcContext;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class CharsheetClientRpc implements ICharsheetRpc {
    @RpcMethodHandler(syncSelf)
    public void onCharsheetSyncSelf(RpcContext ctx, NBTTagCompound nbt) {
        final EntityPlayer self = Minecraft.getMinecraft().player;
        updateCharsheet(self, nbt);
    }

    @RpcMethodHandler(sync)
    public void onCharsheetSync(RpcContext ctx, UUID uuid, NBTTagCompound nbt) {
        final EntityPlayer target = Minecraft.getMinecraft().world.getPlayerEntityByUUID(uuid);
        if (target == null) return;

        updateCharsheet(target, nbt);
    }

    private void updateCharsheet(EntityPlayer target, NBTTagCompound nbt) {
        CharsheetProvider.get(target).replaceWith(CharsheetProvider.decode(nbt));

        target.refreshDisplayName();
    }

    public static void postCharsheet(EntityPlayer player, ICharsheet charsheet) {
        Misca.RPC.sendToServer(post, player.getUniqueID(), CharsheetProvider.encode(charsheet));
    }
}
