package msifeed.misca.content;

import msifeed.misca.Misca;
import msifeed.misca.MiscaThings;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ItemNotification extends Item {
    public static final String ID = "note_sign";

    public ItemNotification() {
        setRegistryName(Misca.MODID, ID);
        setUnlocalizedName(ID);
        setCreativeTab(CreativeTabs.COMBAT);

        MinecraftForge.EVENT_BUS.register(this);

        this.maxStackSize = 16;
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }

    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        final IBlockState state = world.getBlockState(pos);
        final boolean replaceable = state.getBlock().isReplaceable(world, pos);

        if (facing == EnumFacing.DOWN || (!state.getMaterial().isSolid() && !replaceable) || (replaceable && facing != EnumFacing.UP))
            return EnumActionResult.FAIL;

        pos = pos.offset(facing);
        final ItemStack stack = player.getHeldItem(hand);
        if (!player.canPlayerEdit(pos, facing, stack) || !MiscaThings.standingNote.canPlaceBlockAt(world, pos))
            return EnumActionResult.FAIL;

        if (world.isRemote)
            return EnumActionResult.SUCCESS;

        pos = replaceable ? pos.down() : pos;
        if (facing == EnumFacing.UP) {
            final int i = MathHelper.floor((double) ((player.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
            world.setBlockState(pos, MiscaThings.standingNote.getDefaultState().withProperty(BlockStandingNote.ROTATION, i), 11);
        } else {
            world.setBlockState(pos, MiscaThings.wallNote.getDefaultState().withProperty(BlockWallNote.FACING, facing), 11);
        }

        final TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileNotification && !ItemBlock.setTileEntityNBT(world, player, pos, stack)) {
            player.openEditSign((TileNotification) tile);
        }

        if (player instanceof EntityPlayerMP) {
            CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, stack);
        }

        stack.shrink(1);
        return EnumActionResult.SUCCESS;
    }
}
