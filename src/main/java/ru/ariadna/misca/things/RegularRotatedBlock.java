package ru.ariadna.misca.things;

import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.util.IIcon;

public class RegularRotatedBlock extends BlockRotatedPillar {
    public static final String NAME_BASE = "misca_pillar_";

    RegularRotatedBlock(int index) {
        super(Material.rock);

        setBlockName(NAME_BASE + index);
        setBlockTextureName("misca:" + NAME_BASE + index);
        setCreativeTab(MiscaThings.tab);
    }

    @Override
    protected IIcon getSideIcon(int p_150163_1_) {
        return null;
    }

    @Override
    protected IIcon getTopIcon(int p_150161_1_) {
        return super.getTopIcon(p_150161_1_);
    }
}
