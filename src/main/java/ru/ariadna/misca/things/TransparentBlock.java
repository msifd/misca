package ru.ariadna.misca.things;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TransparentBlock extends Block {
    public static final String NAME_BASE = "misca_glass";

    protected TransparentBlock() {
        super(Material.glass);

        setBlockName(NAME_BASE);
        setBlockTextureName("misca:" + NAME_BASE);
        setCreativeTab(MiscaThings.tab);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return true;
    }

    @Override
    public boolean isBlockSolid(IBlockAccess p_149747_1_, int p_149747_2_, int p_149747_3_, int p_149747_4_, int p_149747_5_) {
        return true;
    }

//    @Override
//    public boolean canPlaceBlockOnSide(World p_149707_1_, int p_149707_2_, int p_149707_3_, int p_149707_4_, int p_149707_5_) {
//        return true;
//    }
//
//    @Override
//    public boolean canPlaceTorchOnTop(World world, int x, int y, int z) {
//        return true;
//    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_) {
        return false;
    }
}
