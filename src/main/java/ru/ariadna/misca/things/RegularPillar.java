package ru.ariadna.misca.things;

import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class RegularPillar extends BlockRotatedPillar {
    public static final String NAME_BASE = "misca_pillar_";

    RegularPillar(int index) {
        super(Material.rock);
        setBlockName(NAME_BASE + index);
        setBlockTextureName("misca:" + NAME_BASE + index);
        setCreativeTab(MiscaThings.tab);

        setHardness(2);
        setResistance(10);
        setStepSound(soundTypePiston);
    }

    @Override
    protected IIcon getSideIcon(int p_150163_1_) {
        return this.blockIcon;
    }

    @Override
    public void registerBlockIcons(IIconRegister p_149651_1_) {
        this.blockIcon = p_149651_1_.registerIcon(this.getTextureName());
        this.field_150164_N = p_149651_1_.registerIcon(this.getTextureName() + "_top");
    }
}
