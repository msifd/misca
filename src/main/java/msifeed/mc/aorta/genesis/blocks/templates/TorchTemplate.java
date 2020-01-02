package msifeed.mc.aorta.genesis.blocks.templates;

import msifeed.mc.aorta.genesis.GenesisTrait;
import msifeed.mc.aorta.genesis.blocks.BlockGenesisUnit;
import msifeed.mc.aorta.genesis.blocks.BlockTraitCommons;
import net.minecraft.block.BlockTorch;
import net.minecraft.world.World;

import java.util.Random;

public class TorchTemplate extends BlockTorch implements BlockTraitCommons.Getter {
    private BlockTraitCommons traits;

    public TorchTemplate(BlockGenesisUnit unit) {
        traits = new BlockTraitCommons(unit);
        setBlockName(unit.id);
    }

    @Override
    public BlockTraitCommons getCommons() {
        return traits;
    }

    @Override
    public void randomDisplayTick(World p_149734_1_, int p_149734_2_, int p_149734_3_, int p_149734_4_, Random p_149734_5_) {
        if (traits.unit.hasTrait(GenesisTrait.without_particles))
            return;
        super.randomDisplayTick(p_149734_1_, p_149734_2_, p_149734_3_, p_149734_4_, p_149734_5_);
    }
}
