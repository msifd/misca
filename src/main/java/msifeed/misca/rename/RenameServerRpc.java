package msifeed.misca.rename;

import msifeed.sys.rpc.RpcContext;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;

import java.util.Objects;

public class RenameServerRpc implements IRenameRpc {
    @RpcMethodHandler(rename)
    public void onRename(RpcContext ctx, NBTTagCompound display) {
        final EntityPlayer player = ctx.getServerHandler().player;
        final ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        final NBTTagCompound nbt = Objects.requireNonNull(stack.getTagCompound());
        nbt.setTag("display", display);
    }

    @RpcMethodHandler(clear)
    public void onRename(RpcContext ctx) {
        final EntityPlayer player = ctx.getServerHandler().player;
        final ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);

        final NBTTagCompound display = stack.getSubCompound("display");
        if (display == null) return;

        display.removeTag("Lore");
        stack.clearCustomName();
    }
}
