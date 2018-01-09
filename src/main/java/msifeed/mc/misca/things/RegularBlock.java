package msifeed.mc.misca.things;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

class RegularBlock extends Block {
    RegularBlock(String base_name, int index) {
        super(Material.rock);

        setBlockName(base_name + index);
        setBlockTextureName("misca:" + base_name + index);
        setCreativeTab(MiscaThings.blocksTab);

        setHardness(2);
        setStepSound(soundTypePiston);
    }
}
