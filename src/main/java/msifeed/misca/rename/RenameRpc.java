package msifeed.misca.rename;

import msifeed.misca.Misca;
import msifeed.sys.rpc.RpcContext;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

public class RenameRpc {
    private static final String gui = "rename.gui";
    private static final String rename = "rename.rename";
    private static final String clear = "rename.clear";

    public static void openGui(EntityPlayerMP player) {
        Misca.RPC.sendTo(player, gui);
    }

    public static void sendRename(ItemStack stack) {
        Misca.RPC.sendToServer(rename, stack.getOrCreateSubCompound("display"));
    }

    public static void sendClear() {
        Misca.RPC.sendToServer(clear);
    }

    // Server

    @RpcMethodHandler(RenameRpc.rename)
    public void onRename(RpcContext ctx, NBTTagCompound display) {
        final EntityPlayer player = ctx.getServerHandler().player;
        final ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        final NBTTagCompound nbt = Objects.requireNonNull(stack.getTagCompound());
        nbt.setTag("display", display);
    }

    @RpcMethodHandler(RenameRpc.clear)
    public void onRename(RpcContext ctx) {
        final EntityPlayer player = ctx.getServerHandler().player;
        final ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);

        final NBTTagCompound display = stack.getSubCompound("display");
        if (display == null) return;

        display.removeTag("Lore");
        stack.clearCustomName();
    }

    // Client

    @SideOnly(Side.CLIENT)
    @RpcMethodHandler(gui)
    public void onOpenGui() {
        final EntityPlayer player = Minecraft.getMinecraft().player;
        final ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
        Minecraft.getMinecraft().displayGuiScreen(new GuiScreenRenameItem(stack));
    }
}
