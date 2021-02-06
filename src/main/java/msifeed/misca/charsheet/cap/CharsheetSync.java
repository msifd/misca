package msifeed.misca.charsheet.cap;

import msifeed.misca.Misca;
import msifeed.misca.MiscaPerms;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.sys.rpc.RpcContext;
import msifeed.sys.rpc.RpcException;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class CharsheetSync {
    private static final String post = "charsheet.post";
    private static final String sync = "charsheet.sync";
    private static final String syncSelf = "charsheet.sync.self";

    public static void sync(EntityPlayerMP receiver, EntityLivingBase target) {
        final NBTTagCompound nbt = CharsheetProvider.encode(CharsheetProvider.get(target));
        if (receiver == target)
            Misca.RPC.sendTo(receiver, syncSelf, nbt);
        else
            Misca.RPC.sendTo(receiver, sync, target.getUniqueID(), nbt);
    }

    public static void sync(EntityLivingBase target) {
        final NBTTagCompound nbt = CharsheetProvider.encode(CharsheetProvider.get(target));
        if (target instanceof EntityPlayerMP)
            Misca.RPC.sendTo((EntityPlayerMP) target, syncSelf, nbt);
        Misca.RPC.sendToAllTracking(target, sync, target.getUniqueID(), nbt);
    }

    @RpcMethodHandler(post)
    public void onPost(RpcContext ctx, UUID uuid, NBTTagCompound nbt) {
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
        sync(target);
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(syncSelf)
    public void onSyncSelf(RpcContext ctx, NBTTagCompound nbt) {
        final EntityPlayer self = Minecraft.getMinecraft().player;
        update(self, nbt);
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(sync)
    public void onSync(RpcContext ctx, UUID uuid, NBTTagCompound nbt) {
        final EntityPlayer target = Minecraft.getMinecraft().world.getPlayerEntityByUUID(uuid);
        if (target != null)
            update(target, nbt);
    }

    @SideOnly(Side.CLIENT)
    private void update(EntityPlayer target, NBTTagCompound nbt) {
        CharsheetProvider.get(target).replaceWith(CharsheetProvider.decode(nbt));
        target.refreshDisplayName();
    }

    @SideOnly(Side.CLIENT)
    public static void post(EntityPlayer player, ICharsheet charsheet) {
        Misca.RPC.sendToServer(post, player.getUniqueID(), CharsheetProvider.encode(charsheet));
    }
}
