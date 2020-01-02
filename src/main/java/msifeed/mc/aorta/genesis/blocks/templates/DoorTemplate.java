package msifeed.mc.aorta.genesis.blocks.templates;

import cpw.mods.fml.common.registry.GameRegistry;
import msifeed.mc.aorta.genesis.GenesisCreativeTab;
import msifeed.mc.aorta.genesis.blocks.BlockGenesisUnit;
import msifeed.mc.aorta.genesis.blocks.BlockTraitCommons;
import msifeed.mc.aorta.genesis.blocks.SpecialBlockRegisterer;
import msifeed.mc.aorta.locks.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

public class DoorTemplate extends BlockDoor implements ITileEntityProvider, SpecialBlockRegisterer, BlockTraitCommons.Getter, LockableBlock {
    private BlockTraitCommons traits;
    private final DoorItem item;

    public DoorTemplate(BlockGenesisUnit unit, Material material) {
        super(material);
        traits = new BlockTraitCommons(unit);
        item = new DoorItem(unit, this);

        disableStats();
        setHardness(3);
        setStepSound(material == Material.iron ? soundTypeMetal : soundTypeWood);
        setBlockName(unit.id);
    }

    @Override
    protected boolean canSilkHarvest() {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        final LockObject lock = getLock(world, x, y, z);
        if (lock == null)
            return false;

        if (player.isSneaking() && lock.getLockType() == LockType.DIGITAL) {
            // FIXME: Aorta.GUI_HANDLER.toggleDigitalLock(lock);
            return true;
        } else if (lock.isLocked()) {
            return true;
        }

        return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
    }

    @Override
    public void func_150014_a(World world, int x, int y, int z, boolean flag) {
        // Also check if locked when powered
        final LockObject lock = getLock(world, x, y, z);
        if (lock == null || !lock.isLocked())
            super.func_150014_a(world, x, y, z, flag);
    }

    @Override
    public net.minecraft.item.Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_) {
        return item;
    }

    @Override
    public net.minecraft.item.Item getItemDropped(int meta, Random rand, int p_149650_3_) {
        return (meta & 8) != 0 ? null : item;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new LockTileEntity();
    }

    @Override
    public BlockTraitCommons getCommons() {
        return traits;
    }

    @Override
    public void register(String id) {
        item.setCreativeTab(GenesisCreativeTab.BLOCKS);
        GameRegistry.registerBlock(this, ItemBlockTemplate.class, id);
        GameRegistry.registerItem(item, id + "_item");
    }

    @Override
    public TileEntity getLockTileEntity(World world, int x, int y, int z) {
        final Block middleBlock = world.getBlock(x, y, z);

        if (!(middleBlock instanceof DoorTemplate))
            return null;

        final Block upperBlock = world.getBlock(x, y + 1, z);
        final int groundBlockY = upperBlock instanceof DoorTemplate ? y : y - 1;

        final TileEntity te = world.getTileEntity(x, groundBlockY, z);
        if (te instanceof Lockable)
            return te;
        else
            return null;
    }

    public static class DoorItem extends ItemDoor {
        private final DoorTemplate block;

        DoorItem(BlockGenesisUnit unit, DoorTemplate block) {
            super(Material.wood);
            this.block = block;

            setUnlocalizedName(unit.id);
            setTextureName(unit.textureString + "_item");
        }

        @Override
        public String getItemStackDisplayName(ItemStack itemStack) {
            return BlockTraitCommons.getItemStackDisplayName(block, itemStack);
        }

        public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
            if (side != 1) {
                return false;
            } else {
                ++y;
                if (player.canPlayerEdit(x, y, z, side, stack) && player.canPlayerEdit(x, y + 1, z, side, stack)) {
                    if (!block.canPlaceBlockAt(world, x, y, z)) {
                        return false;
                    } else {
                        int i1 = MathHelper.floor_double((double) ((player.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;
                        placeDoorBlock(world, x, y, z, i1, block);
                        --stack.stackSize;
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }
    }
}
