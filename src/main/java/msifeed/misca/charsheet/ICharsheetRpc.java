package msifeed.misca.charsheet;

import msifeed.misca.Misca;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.charsheet.cap.ICharsheet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;

public interface ICharsheetRpc {
    String update = "charsheet.update";

    static void updateCharsheet(EntityPlayer player, ICharsheet charsheet) {
        final Capability<ICharsheet> cap = CharsheetProvider.CAP;
        final NBTTagCompound nbt = (NBTTagCompound) cap.getStorage().writeNBT(cap, charsheet, null);
        Misca.RPC.sendToServer(update, player.getUniqueID(), nbt);
    }
}
