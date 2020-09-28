package msifeed.misca.charsheet;

import msifeed.misca.Misca;
import msifeed.misca.MiscaPerms;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.sys.rpc.RpcException;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class CharsheetServerRpc implements ICharsheetRpc {
    @RpcMethodHandler(post)
    public void onCharsheetPost(MessageContext ctx, UUID uuid, NBTTagCompound nbt) {
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

        Misca.RPC.sendTo(target, syncSelf, nbt);
//        Misca.RPC.sendToDimension(target.dimension, sync, uuid, nbt);
        Misca.RPC.sendToAllTracking(target, sync, uuid, nbt);
    }
}
