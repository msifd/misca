package msifeed.misca.genesis.blocks.templates;

import msifeed.misca.genesis.blocks.BlockRule;
import msifeed.misca.genesis.blocks.tiles.TileEntityGenesisContainer;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockContainerTemplate extends BlockContainer implements IBlockTemplate {
    private final BlockRule rule;

    public BlockContainerTemplate(BlockRule rule) {
        super(Material.WOOD);
        this.rule = rule;
    }

    @Override
    public BlockRule getRule() {
        return rule;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityGenesisContainer(rule);
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote)
            return true;

        final TileEntity tile = worldIn.getTileEntity(pos);
        if (!(tile instanceof TileEntityGenesisContainer))
            return false;
        playerIn.displayGUIChest((ILockableContainer) tile);

        return true;
    }
}
