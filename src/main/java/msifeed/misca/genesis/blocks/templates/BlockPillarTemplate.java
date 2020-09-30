package msifeed.misca.genesis.blocks.templates;

import msifeed.misca.genesis.blocks.BlockRule;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;

public class BlockPillarTemplate extends BlockRotatedPillar implements IBlockTemplate {
    private final BlockRule rule;

    public BlockPillarTemplate(BlockRule rule) {
        super(Material.WOOD);
        this.rule = rule;
    }

    @Override
    public BlockRule getRule() {
        return rule;
    }
}
