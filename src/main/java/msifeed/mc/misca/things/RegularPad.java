package msifeed.mc.misca.things;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

class RegularPad extends Block {
    RegularPad(String base_name, int index) {
        super(Material.rock);

        setBlockName(base_name + index);
        setBlockTextureName("misca:" + base_name + index);
        setCreativeTab(MiscaThings.blocksTab);

        float f = 0.5F;
        float f1 = 0.015625F;
        setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
    }

    public int getRenderType() {
        return 23;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }
}
