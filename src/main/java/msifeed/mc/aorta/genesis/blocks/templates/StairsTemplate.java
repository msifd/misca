package msifeed.mc.aorta.genesis.blocks.templates;

import msifeed.mc.aorta.genesis.blocks.BlockTraitCommons;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;

public class StairsTemplate extends BlockStairs implements BlockTraitCommons.Getter {
    private final BlockTraitCommons traits;

    public StairsTemplate(Block parent, String id, BlockTraitCommons traits) {
        super(parent, 0);
        this.traits = traits;
        setBlockName(id);
    }

    @Override
    public BlockTraitCommons getCommons() {
        return traits;
    }
}
