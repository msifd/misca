package msifeed.misca.rename;

import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Objects;

public class RenameServerRpc implements IRenameRpc {
    @RpcMethodHandler(rename)
    public void onRename(MessageContext ctx, NBTTagCompound display) {
        final EntityPlayer player = Minecraft.getMinecraft().player;
        final ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        final NBTTagCompound nbt = Objects.requireNonNull(stack.getTagCompound());
        nbt.setTag("display", display);
    }

    @RpcMethodHandler(clear)
    public void onRename(MessageContext ctx) {
        final EntityPlayer player = Minecraft.getMinecraft().player;
        final ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);

        final NBTTagCompound display = stack.getSubCompound("display");
        if (display == null) return;

        display.removeTag("Lore");
        stack.clearCustomName();
    }
}
