package msifeed.misca.charsheet;

import msifeed.misca.MiscaPerms;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.sys.rpc.RpcContext;
import msifeed.sys.rpc.RpcException;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class CharsheetServerRpc implements ICharsheetRpc {
    @RpcMethodHandler(post)
    public void onCharsheetPost(RpcContext ctx, UUID uuid, NBTTagCompound nbt) {
        final EntityPlayerMP sender = ctx.getServerHandler().player;
        final EntityPlayerMP target;

        if (sender.getUniqueID().equals(uuid))
            target = sender;
        else if (MiscaPerms.isGameMaster(sender))
            target = (EntityPlayerMP) sender.world.getPlayerEntityByUUID(uuid);
        else
            throw new RpcException(sender, "Not a GameMaster!");

        if (target == null)
            throw new RpcException(sender, "Target is missing.");

        CharsheetProvider.get(target).replaceWith(CharsheetProvider.decode(nbt));

        ctx.rpc.sendTo(target, syncSelf, nbt);
        ctx.rpc.sendToAllTracking(target, sync, uuid, nbt);
    }
}
