package msifeed.mc.misca.things;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;

public class RegularStairs extends BlockStairs {
    private final String parentName;

    RegularStairs(Block parent, int meta) {
        super(parent, meta);
        this.parentName = parent.getUnlocalizedName().substring(5); // Remove `tile.`

        setBlockName(getName());
        setStepSound(parent.stepSound);
        setCreativeTab(MiscaThings.blocksTab);
    }

    public String getName() {
        return parentName + "_stairs";
    }
}
