package msifeed.misca.charsheet.client;

import msifeed.misca.charsheet.ICharsheetRpc;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.charsheet.cap.ICharsheet;
import msifeed.misca.MiscaPerms;
import msifeed.sys.rpc.RpcException;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.server.permission.PermissionAPI;

import java.util.UUID;

public class CharsheetClientRpc implements ICharsheetRpc {
    @RpcMethodHandler(update)
    public void onCharsheetUpdate(MessageContext ctx, UUID uuid, NBTTagCompound nbt) {
        final EntityPlayerMP sender = ctx.getServerHandler().player;
        final EntityPlayer target;

        if (sender.getUniqueID() == uuid)
            target = sender;
        else if (MiscaPerms.isGameMaster(sender))
            target = sender.world.getPlayerEntityByUUID(uuid);
        else
            throw new RpcException(sender, "Not a GameMaster!");

        if (target == null)
            throw new RpcException(sender, "Target is missing.");

        final Capability<ICharsheet> cap = CharsheetProvider.CAP;
        final ICharsheet newCharsheet = cap.getDefaultInstance();
        cap.getStorage().readNBT(cap, newCharsheet, null, nbt);

        target.getCapability(cap, null).replaceWith(newCharsheet);
    }
}
