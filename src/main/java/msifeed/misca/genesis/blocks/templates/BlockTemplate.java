package msifeed.misca.genesis.blocks.templates;

import msifeed.misca.genesis.blocks.BlockRule;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockTemplate extends Block implements IBlockTemplate {
    private final BlockRule rule;

    public BlockTemplate(BlockRule rule) {
        super(Material.WOOD);
        this.rule = rule;
    }

    @Override
    public BlockRule getRule() {
        return rule;
    }
}
