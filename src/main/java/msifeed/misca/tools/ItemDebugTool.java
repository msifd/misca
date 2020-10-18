package msifeed.misca.tools;

import msifeed.misca.Misca;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDebugTool extends Item {
    public static final String ID = "misca_debug_tool";

    public ItemDebugTool() {
        setRegistryName(Misca.MODID, ID);
        setUnlocalizedName(ID);
        setCreativeTab(CreativeTabs.TOOLS);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
