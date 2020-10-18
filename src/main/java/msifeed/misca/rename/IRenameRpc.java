package msifeed.misca.rename;

import msifeed.misca.Misca;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public interface IRenameRpc {
    String gui = "rename.gui";
    String rename = "rename.rename";
    String clear = "rename.clear";

    static void openGui(EntityPlayerMP player) {
        Misca.RPC.sendTo(player, gui);
    }

    static void sendRename(ItemStack stack) {
        Misca.RPC.sendToServer(rename, stack.getOrCreateSubCompound("display"));
    }

    static void sendClear() {
        Misca.RPC.sendToServer(clear);
    }
}
