package msifeed.misca.charsheet.cap;

import msifeed.misca.Misca;
import msifeed.misca.MiscaPerms;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.sys.rpc.RpcContext;
import msifeed.sys.rpc.RpcException;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class CharsheetSync {
    private static final String post = "charsheet.post";
    private static final String sync = "charsheet.sync";
    private static final String syncSelf = "charsheet.syncSelf";

    public static void sync(EntityPlayerMP receiver, EntityPlayer target) {
        final NBTTagCompound nbt = CharsheetProvider.encode(CharsheetProvider.get(target));
        if (receiver == target)
            Misca.RPC.sendTo(receiver, syncSelf, nbt);
        else
            Misca.RPC.sendTo(receiver, sync, target.getUniqueID(), nbt);
    }

    public static void sync(EntityPlayer target) {
        final NBTTagCompound nbt = CharsheetProvider.encode(CharsheetProvider.get(target));
        if (target instanceof EntityPlayerMP)
            Misca.RPC.sendTo((EntityPlayerMP) target, syncSelf, nbt);
        Misca.RPC.sendToAllTracking(target, sync, target.getUniqueID(), nbt);
    }

    @RpcMethodHandler(post)
    public void onPost(RpcContext ctx, int entityId, NBTTagCompound nbt) {
        final EntityPlayerMP sender = ctx.getServerHandler().player;
        if (sender.getEntityId() != entityId && !MiscaPerms.isGameMaster(sender))
            throw new RpcException(sender, "Not a GameMaster!");

        final Entity targetEntity = sender.world.getEntityByID(entityId);
        if (!(targetEntity instanceof EntityPlayer))
            throw new RpcException(sender, "Target is not found!");

        final EntityPlayer target = (EntityPlayer) targetEntity;
        CharsheetProvider.get(target).replaceWith(CharsheetProvider.decode(nbt));
        sync(target);

        sender.sendStatusMessage(new TextComponentString("Charsheet changed"), true);
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
        CharsheetProvider.get(target).replaceWith(CharsheetProvider.decode(nbt));
        target.refreshDisplayName();
    }

    @SideOnly(Side.CLIENT)
    public static void post(EntityPlayer target, ICharsheet charsheet) {
        Misca.RPC.sendToServer(post, target.getEntityId(), CharsheetProvider.encode(charsheet));
    }
}
