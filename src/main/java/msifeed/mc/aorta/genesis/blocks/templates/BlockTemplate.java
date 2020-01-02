package msifeed.mc.aorta.genesis.blocks.templates;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.mc.aorta.genesis.blocks.BlockGenesisUnit;
import msifeed.mc.aorta.genesis.blocks.BlockTraitCommons;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class BlockTemplate extends Block implements BlockTraitCommons.Getter {
    private BlockTraitCommons traits;

    public BlockTemplate(BlockGenesisUnit unit, Material material) {
        super(material);
        traits = new BlockTraitCommons(unit);
        setBlockName(unit.id);
    }

    @Override
    public BlockTraitCommons getCommons() {
        return traits;
    }

    @Override
    public int getRenderType() {
        return traits.getRenderType();
    }

    @Override
    public int getRenderBlockPass() {
        return traits.getRenderBlockPass();
    }

    @Override
    public boolean isOpaqueCube() {
        return traits != null && traits.isOpaqueCube();
    }

    @Override
    public int getLightOpacity() {
        return isOpaqueCube() ? 255 : 0;
    }

    @Override
    public boolean isBlockSolid(IBlockAccess access, int x, int y, int z, int side) {
        return traits.isSolid(side, access.getBlockMetadata(x, y, z));
    }

    @Override
    public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
        return traits.isLadder();
    }

    @Override
    public boolean isLeaves(IBlockAccess world, int x, int y, int z) {
        return traits.isLeaves();
    }

    @Override
    public boolean renderAsNormalBlock() {
        return traits.renderAsNormalBlock();
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
        return traits.shouldSideBeRendered(blockAccess, x, y, z, side);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        traits.updateTick(this, world, x, y, z, rand);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        traits.onEntityCollidedWithBlock(this, world, x, y, z, entity);
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
        return traits.onBlockPlaced(side, meta);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemStack) {
        traits.onBlockPlacedBy(world, x, y, z, entity);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z) {
        traits.setBlockBoundsBasedOnState(access, x, y, z);
    }

    @Override
    public void setBlockBoundsForItemRender() {
        if (traits.half) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity entity) {
        if (traits.half)
            this.setBlockBoundsBasedOnState(world, x, y, z);
        super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        if (traits.isNotCollidable())
            return null;
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (traits.textureLayout == null)
            return super.getIcon(side, meta);
        return traits.getIcon(side, meta);
    }

    @Override
    public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side) {
        return super.getIcon(access, x, y, z, side);
    }

    @Override
    public void registerBlockIcons(IIconRegister register) {
        if (traits.textureLayout != null)
            traits.textureLayout.registerBlockIcons(register);
        else
            super.registerBlockIcons(register);
    }
}
