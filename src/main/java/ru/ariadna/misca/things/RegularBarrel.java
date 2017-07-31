package ru.ariadna.misca.things;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class RegularBarrel extends Block {
    public static final String NAME_BASE = "misca_barrel_";
    private IIcon bottomIcon;
    private IIcon topIcon;

    protected RegularBarrel(int index) {
        super(Material.rock);

        setBlockName(NAME_BASE + index);
        setBlockTextureName("misca:" + NAME_BASE + index);
        setCreativeTab(MiscaThings.tab);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        switch (side) {
            case 0:
                return bottomIcon;
            case 1:
                return topIcon;
            default:
                return blockIcon;
        }
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.blockIcon = iconRegister.registerIcon(this.getTextureName());
        this.bottomIcon = iconRegister.registerIcon(this.getTextureName() + "_bottom");
        this.topIcon = iconRegister.registerIcon(this.getTextureName() + "_top");
    }
}
