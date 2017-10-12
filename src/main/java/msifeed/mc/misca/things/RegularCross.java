package msifeed.mc.misca.things;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

class RegularCross extends Block {
    RegularCross(String base_name, int index) {
        super(Material.web);

        setBlockName(base_name + index);
        setBlockTextureName("misca:" + base_name + index);
        setCreativeTab(MiscaThings.tab);

        setLightOpacity(1);
    }

    public int getRenderType()
    {
        return 1;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }
}
