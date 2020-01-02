package msifeed.mc.aorta.genesis.blocks.templates;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.mc.aorta.genesis.GenesisCreativeTab;
import msifeed.mc.aorta.genesis.blocks.BlockGenesisUnit;
import msifeed.mc.aorta.genesis.blocks.BlockTraitCommons;
import msifeed.mc.aorta.genesis.blocks.SpecialBlockRegisterer;
import net.minecraft.block.BlockBed;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

public class BedTemplate extends BlockBed implements SpecialBlockRegisterer {
    final BedItem item;
    private IIcon[] texture_top, texture_end, texture_side;

    public BedTemplate(BlockGenesisUnit unit) {
        item = new BedItem(unit, this);

        setBlockName(unit.id);
        setBlockTextureName(unit.textureString);

        disableStats();
        setHardness(0.2F);
    }

    @Override
    public net.minecraft.item.Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_) {
        return item;
    }

    @Override
    public net.minecraft.item.Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return item;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        if (p_149691_1_ == 0) {
            return Blocks.planks.getBlockTextureFromSide(p_149691_1_);
        } else {
            int k = getDirection(p_149691_2_);
            int l = Direction.bedDirection[k][p_149691_1_];
            int i1 = isBlockHeadOfBed(p_149691_2_) ? 1 : 0;
            return (i1 != 1 || l != 2) && (i1 != 0 || l != 3)
                    ? (l != 5 && l != 4 ? texture_top[i1] : texture_side[i1]) : texture_end[i1];
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_) {
        texture_top = new IIcon[]{
                p_149651_1_.registerIcon(this.getTextureName() + "_feet_top"),
                p_149651_1_.registerIcon(this.getTextureName() + "_head_top")
        };
        texture_end = new IIcon[]{
                p_149651_1_.registerIcon(this.getTextureName() + "_feet_end"),
                p_149651_1_.registerIcon(this.getTextureName() + "_head_end")
        };
        texture_side = new IIcon[]{
                p_149651_1_.registerIcon(this.getTextureName() + "_feet_side"),
                p_149651_1_.registerIcon(this.getTextureName() + "_head_side")
        };
    }

    @Override
    public void register(String id) {
        item.setCreativeTab(GenesisCreativeTab.BLOCKS);
        GameRegistry.registerBlock(this, ItemBlockTemplate.class, id);
        GameRegistry.registerItem(item, id + "_item");
    }

    private static class BedItem extends net.minecraft.item.Item {
        private final BedTemplate block;

        BedItem(BlockGenesisUnit unit, BedTemplate block) {
            this.block = block;

            setUnlocalizedName(unit.id);
            setTextureName(unit.textureString);
        }

        @Override
        public String getItemStackDisplayName(ItemStack itemStack) {
            return BlockTraitCommons.getItemStackDisplayName(block, itemStack);
        }

        public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_) {
            if (world.isRemote) return true;
            else if (p_77648_7_ != 1) return false;

            ++y;
            final int i1 = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            final byte b0;
            final byte b1;

            if (i1 == 1) b0 = -1;
            else if (i1 == 3) b0 = 1;
            else b0 = 0;
            if (i1 == 0) b1 = 1;
            else if (i1 == 2) b1 = -1;
            else b1 = 0;

            if (player.canPlayerEdit(x, y, z, p_77648_7_, stack) && player.canPlayerEdit(x + b0, y, z + b1, p_77648_7_, stack)) {
                if (world.isAirBlock(x, y, z) && world.isAirBlock(x + b0, y, z + b1)
                        && World.doesBlockHaveSolidTopSurface(world, x, y - 1, z)
                        && World.doesBlockHaveSolidTopSurface(world, x + b0, y - 1, z + b1)
                        ) {
                    world.setBlock(x, y, z, block, i1, 3);
                    if (world.getBlock(x, y, z) == block)
                        world.setBlock(x + b0, y, z + b1, block, i1 + 8, 3);

                    --stack.stackSize;
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }
}
