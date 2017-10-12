package msifeed.mc.misca.things;

import net.minecraft.block.BlockBrewingStand;

public class RegularBrewingStand extends BlockBrewingStand {
    public static final String NAME_BASE = "misca_brewing_stand_";

    RegularBrewingStand(int index) {
        setBlockName(NAME_BASE + index);
        setBlockTextureName("misca:" + NAME_BASE + index);
        setCreativeTab(MiscaThings.tab);

        setLightOpacity(255);
        setHardness(2);
        setResistance(10);
        setStepSound(soundTypePiston);
    }
}
