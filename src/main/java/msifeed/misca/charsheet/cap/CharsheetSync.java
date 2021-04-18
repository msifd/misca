package msifeed.misca.charsheet.cap;

import msifeed.misca.Misca;
import msifeed.misca.MiscaPerms;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.sys.rpc.RpcContext;
import msifeed.sys.rpc.RpcException;
import msifeed.sys.rpc.RpcMethodHandler;
import msifeed.sys.rpc.RpcUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CharsheetSync {
    private static final String post = "charsheet.post";
    private static final String sync = "charsheet.sync";

    public static void sync(EntityPlayerMP receiver, EntityPlayer target) {
        final NBTTagCompound nbt = CharsheetProvider.encode(CharsheetProvider.get(target));
        Misca.RPC.sendTo(receiver, sync, target.getEntityId(), nbt);
    }

    public static void sync(EntityPlayer target) {
        final NBTTagCompound nbt = CharsheetProvider.encode(CharsheetProvider.get(target));
        Misca.RPC.sendToAllAround(target, sync, target.getEntityId(), nbt);
    }

    @RpcMethodHandler(post)
    public void onPost(RpcContext ctx, int eid, NBTTagCompound nbt) {
        final EntityPlayerMP sender = ctx.getServerHandler().player;
        if (sender.getEntityId() != eid && !MiscaPerms.isGameMaster(sender))
            throw new RpcException(sender, "Not a GameMaster!");

        final Entity targetEntity = sender.world.getEntityByID(eid);
        if (!(targetEntity instanceof EntityPlayer))
            throw new RpcException(sender, "Target is not found!");

        final EntityPlayer target = (EntityPlayer) targetEntity;
        CharsheetProvider.get(target).replaceWith(CharsheetProvider.decode(nbt));
        target.refreshDisplayName();
        sync(target);

        sender.sendStatusMessage(new TextComponentString("Charsheet changed"), true);
    }

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(sync)
    public void onSync(int eid, NBTTagCompound nbt) {
        final EntityPlayer target = RpcUtils.findPlayer(Minecraft.getMinecraft().world, eid);
        if (target == null) return;

        CharsheetProvider.get(target).replaceWith(CharsheetProvider.decode(nbt));
        target.refreshDisplayName();
    }

    // // // //

    @SideOnly(Side.CLIENT)
    public static void post(EntityPlayer target, ICharsheet charsheet) {
        Misca.RPC.sendToServer(post, target.getEntityId(), CharsheetProvider.encode(charsheet));
    }
}
