package msifeed.mc.misca.things;

import net.minecraft.block.BlockTorch;

public class RegularTorch extends BlockTorch {
    RegularTorch(String name_base, int index) {
        setBlockName(name_base + index);
        setBlockTextureName("misca:" + name_base + index);
        setCreativeTab(MiscaThings.blocksTab);

        setHardness(0F);
        setStepSound(soundTypeWood);
    }
}
