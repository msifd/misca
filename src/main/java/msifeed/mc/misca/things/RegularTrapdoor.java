package msifeed.mc.misca.things;

import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;

public class RegularTrapdoor extends BlockTrapDoor {
    RegularTrapdoor(String base_name, int index) {
        super(Material.wood);

        setBlockName(base_name + index);
        setBlockTextureName("misca:" + base_name + index);
        setCreativeTab(MiscaThings.tab);

        disableStats();
        setHardness(3.0F);
        setStepSound(soundTypeWood);
    }
}
