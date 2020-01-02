package msifeed.mc.aorta.genesis.blocks.templates;

import msifeed.mc.aorta.genesis.blocks.BlockGenesisUnit;
import msifeed.mc.aorta.genesis.blocks.BlockTraitCommons;
import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class PaneTemplate extends BlockPane implements BlockTraitCommons.Getter {
    private BlockTraitCommons traits;

    public PaneTemplate(BlockGenesisUnit unit, Material material) {
        super(unit.textureString, unit.textureString + "_top", material, false);
        traits = new BlockTraitCommons(unit);
        setBlockName(unit.id);
    }

    @Override
    public BlockTraitCommons getCommons() {
        return traits;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_) {
        if (traits.isNotCollidable())
            return null;
        return super.getCollisionBoundingBoxFromPool(p_149668_1_, p_149668_2_, p_149668_3_, p_149668_4_);
    }
}
