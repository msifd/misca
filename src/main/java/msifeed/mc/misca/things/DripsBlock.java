package msifeed.mc.misca.things;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public class DripsBlock extends Block {
    static final String NAME_BASE = "misca_drips";

    DripsBlock() {
        super(Material.rock);

        setBlockName(NAME_BASE);
        setCreativeTab(MiscaThings.tab);

        setBlockBounds(0.2f, 0, 0.2f, 0.8f, 0.8f, 0.8f);

        setBlockUnbreakable();
        setResistance(6000000.0F);
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

    @Override
    public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        if (rand.nextInt(5) == 0 && !world.getBlock(x, y - 2, z).getMaterial().blocksMovement()) {
            double px = (double) ((float) x + rand.nextFloat());
            double py = (double) y - 1.05D;
            double pz = (double) ((float) z + rand.nextFloat());
            world.spawnParticle("dripWater", px, py, pz, 0.0D, 0.0D, 0.0D);
        }
    }
}
