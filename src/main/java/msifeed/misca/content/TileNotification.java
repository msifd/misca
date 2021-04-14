package msifeed.misca.content;

import msifeed.misca.Misca;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ResourceLocation;

public class TileNotification extends TileEntitySign {
    public static final ResourceLocation ID = new ResourceLocation(Misca.MODID, "transparent_sign");

    transient int[] lineWidth = null;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        lineWidth = null;
    }
}
