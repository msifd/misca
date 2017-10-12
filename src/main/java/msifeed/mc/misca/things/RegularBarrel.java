package msifeed.mc.misca.things;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class RegularBarrel extends Block {
    private IIcon bottomIcon;
    private IIcon topIcon;

    RegularBarrel(String name_base, int index) {
        super(Material.rock);

        setBlockName(name_base + index);
        setBlockTextureName("misca:" + name_base + index);
        setCreativeTab(MiscaThings.tab);

        setHardness(2);
        setResistance(10);
        setStepSound(soundTypePiston);
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
