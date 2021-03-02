package msifeed.misca.locks.items;

import msifeed.misca.locks.LockItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LockModels {
    public static final ModelResourceLocation MECH_LOC_MODEL = getModel(LockItems.lockMechanical.getRegistryName());
    public static final ModelResourceLocation MAGI_LOC_MODEL = getModel(LockItems.lockMagical.getRegistryName());

    public static ModelResourceLocation getModel(ResourceLocation location) {
        return new ModelResourceLocation(location, "inventory");
    }
}
