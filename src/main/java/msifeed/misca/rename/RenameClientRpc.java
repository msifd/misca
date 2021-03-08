package msifeed.misca.rename;

import msifeed.misca.client.GuiScreenRenameItem;
import msifeed.sys.rpc.RpcMethodHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class RenameClientRpc implements IRenameRpc {
    @RpcMethodHandler(gui)
    public void onOpenGui() {
        final EntityPlayer player = Minecraft.getMinecraft().player;
        final ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
        Minecraft.getMinecraft().displayGuiScreen(new GuiScreenRenameItem(stack));
    }
}
