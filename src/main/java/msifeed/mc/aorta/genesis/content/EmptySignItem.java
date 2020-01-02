package msifeed.mc.aorta.genesis.content;

import msifeed.mc.aorta.genesis.GenesisCreativeTab;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EmptySignItem extends Item {
    public EmptySignItem() {
        this.maxStackSize = 16;
        this.setCreativeTab(GenesisCreativeTab.BLOCKS);

        setUnlocalizedName("empty_sign");
        setTextureName("minecraft:sign");
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (side == 0) {
            return false;
        } else if (!world.getBlock(x, y, z).getMaterial().isSolid()) {
            return false;
        } else {
            if (side == 1)
                ++y;
            else if (side == 2)
                --z;
            else if (side == 3)
                ++z;
            else if (side == 4)
                --x;
            else if (side == 5)
                ++x;

            if (!player.canPlayerEdit(x, y, z, side, stack)) {
                return false;
            } else if (!EmptySignBlock.standing_empty_sign.canPlaceBlockAt(world, x, y, z)) {
                return false;
            } else if (world.isRemote) {
                return true;
            } else {
                if (side == 1) {
                    int i1 = MathHelper.floor_double((double) ((player.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
                    world.setBlock(x, y, z, EmptySignBlock.standing_empty_sign, i1, 3);
                } else {
                    world.setBlock(x, y, z, EmptySignBlock.wall_empty_sign, side, 3);
                }

                --stack.stackSize;
                TileEntitySign tileentitysign = (TileEntitySign) world.getTileEntity(x, y, z);

                if (tileentitysign != null) {
                    player.func_146100_a(tileentitysign);
                }

                return true;
            }
        }
    }

}
