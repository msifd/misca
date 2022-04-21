package msifeed.misca.content;

import msifeed.misca.Misca;
import msifeed.misca.MiscaThings;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockWallNote extends BlockWallSign {
    public static final String ID = "wall_note";

    public BlockWallNote() {
        setRegistryName(Misca.MODID, ID);
        setUnlocalizedName(ID);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileNotification();
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return MiscaThings.itemNote;
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(MiscaThings.itemNote);
    }
}
