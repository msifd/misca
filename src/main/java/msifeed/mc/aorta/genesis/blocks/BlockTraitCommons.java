package msifeed.mc.aorta.genesis.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.mc.aorta.genesis.GenesisTrait;
import msifeed.mc.aorta.genesis.blocks.client.GenesisBlockRenderer;
import msifeed.mc.misca.database.DBHandler;
import msifeed.mc.misca.utils.MiscaUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockTraitCommons {
    private static final int[] ROTATION_MATRIX = new int[]{
            2, 3, 0, 1, 5, 4,
            3, 2, 1, 0, 5, 4,
            0, 1, 2, 3, 4, 5,
            0, 1, 3, 2, 5, 4,
            0, 1, 5, 4, 2, 3,
            0, 1, 4, 5, 3, 2
    };

    public BlockGenesisUnit unit;
    public BlockTextureLayout textureLayout = null;
    public boolean half = false;
    public boolean notCollidable = false;
    public boolean transparent = false;
    public boolean useAlphaChannel = false;
    public Type type = Type.SIMPLE;
    public Size size = Size.MEDIUM;

    public BlockTraitCommons(BlockGenesisUnit unit) {
        this.unit = unit;
    }

    public static String getItemStackDisplayName(Block block, ItemStack itemStack) {
        if (!(block instanceof BlockTraitCommons.Getter))
            return defaultItemStackDisplayName(itemStack);

        final BlockTraitCommons commons = ((BlockTraitCommons.Getter) block).getCommons();
        return commons.unit.title != null
                ? commons.unit.title
                : defaultItemStackDisplayName(itemStack);
    }

    public static String defaultItemStackDisplayName(ItemStack itemStack) {
        return StatCollector.translateToLocal(itemStack.getItem().getUnlocalizedName(itemStack) + ".name");
    }

    public static int getRotatedOrt(int meta) {
        return (meta & 7) - 1; // Minus default mode for item render
    }

    public static int getPillarOrt(int meta) {
        final int t = meta & 12;
        final int f = meta % 2;
        switch (t) {
            default:
                return f;
            case 8:
                return 2 | f;
            case 4:
                return 4 | f;
        }
    }

    public static int getRotatedSide(int side, int meta) {
        int ort = getRotatedOrt(meta);
        if (ort >= 0) {
            side = ROTATION_MATRIX[ort * 6 + side];
        }
        return side;
    }

    public static int getRotatableMeta(int ort) {
        // Zero is default side alignment used in inventory and etc. so add 1. Subtracted in getRotatableIcon.
        return ort + 1;
    }

    public static int getPillarMeta(int side, int meta) {
        byte b = (byte) (side % 2);
        switch (side) {
            case 2:
            case 3:
                b |= 8;
                break;
            case 4:
            case 5:
                b |= 4;
                break;
        }
        return meta | b;
    }

    public boolean isOpaqueCube() {
        return !half && type != Type.CROSS && !transparent;
    }

    public boolean renderAsNormalBlock() {
        return isOpaqueCube();
    }

    public boolean isNotCollidable() {
        return notCollidable || unit.trapData != null;
    }

    public boolean isSolid(int side, int meta) {
        if (half)
            return side == Facing.oppositeSide[getOrt(meta)];
        return true;
    }

    public boolean isLadder() {
        return unit.hasTrait(GenesisTrait.ladder);
    }

    public boolean isLeaves() {
        return unit.hasTrait(GenesisTrait.leaves);
    }

    public int getRenderType() {
        switch (type) {
            case CROSS:
                return 1;
            case PILLAR:
                return 31;
            case ROTATABLE:
                return GenesisBlockRenderer.ROTATABLE;
            default:
                return 0;
        }
    }

    public int getRenderBlockPass() {
        return useAlphaChannel ? 1 : 0;
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
        if (!transparent)
            return true;

        final Block block = blockAccess.getBlock(x, y, z);

        final boolean otherTransparent;
        if (block instanceof Getter) {
            otherTransparent = ((Getter) block).getCommons().transparent;
        } else {
            otherTransparent = block == Blocks.glass || block == Blocks.stained_glass;
        }

        return !otherTransparent;
    }

    public int onBlockPlaced(int side, int meta) {
        if (type == Type.PILLAR)
            return getPillarMeta(side, meta);
        return meta;
    }

    public void updateTick(Block block, World world, int x, int y, int z, Random rand) {
        if (!world.isRemote && unit.trapData != null) {
            final int meta = world.getBlockMetadata(x, y, z);
            if (meta > 0) {
                world.setBlockMetadataWithNotify(x, y, z, meta - 1, 4);
                world.scheduleBlockUpdate(x, y, z, block, meta);
            }
        }
    }

    public void onEntityCollidedWithBlock(Block block, World world, int x, int y, int z, Entity entity) {
        if (!world.isRemote && unit.trapData != null && entity instanceof EntityPlayer) {
            final int meta = world.getBlockMetadata(x, y, z);
            if (meta == 0) {
                final EntityPlayer player = (EntityPlayer) entity;
                if (!unit.trapData.farMessage.isEmpty())
                    sendEnvMessage(player, unit.trapData.farMessage, unit.trapData.farRadius);
                if (!unit.trapData.closeMessage.isEmpty())
                    sendEnvMessage(player, String.format(unit.trapData.closeMessage, player.getDisplayName()), unit.trapData.closeRadius);
                if (unit.trapData.destroy)
                    world.setBlockToAir(x, y, z);
                world.scheduleBlockUpdate(x, y, z, block, 20);
            }
            world.setBlockMetadataWithNotify(x, y, z, 4, 4);
        }
    }

    private void sendEnvMessage(EntityPlayer center, String text, int radius) {
        MiscaUtils.notifyAround(center, radius, new ChatComponentText(text));
        DBHandler.INSTANCE.logMessage(center, "log", text);
    }

    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity) {
        if (type == Type.ROTATABLE) {
            final int placedOrt = BlockPistonBase.determineOrientation(world, x, y, z, entity);
            final int layoutMeta = getRotatableMeta(placedOrt);
            world.setBlockMetadataWithNotify(x, y, z, layoutMeta, 0);
        }
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        switch (type) {
            default:
                return textureLayout.getIcon(side);
            case PILLAR:
                return textureLayout.getPillarIcon(side, meta);
            case ROTATABLE:
                if (meta == 0) // For item render
                    return textureLayout.getRotatableIcon(side, half ? 2 : 4);
                return textureLayout.getRotatableIcon(side, meta);
        }
    }

    public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z) {
        if (half) {
            final int ort = getOrt(access.getBlockMetadata(x, y, z));
            final float halfSize;
            switch (size) {
                case TINY:
                    halfSize = 0.0625f;
                    break;
                case SMALL:
                    halfSize = 0.25f;
                    break;
                case MEDIUM:
                default:
                    halfSize = 0.5f;
                    break;
                case LARGE:
                    halfSize = 0.75f;
                    break;
            }

            final float value = ort % 2 == 0 ? 1 - halfSize : halfSize;
            final float minY = ort == 0 ? value : 0;
            final float minZ = ort == 2 ? value : 0;
            final float minX = ort == 4 ? value : 0;
            final float maxY = ort == 1 ? value : 1;
            final float maxZ = ort == 3 ? value : 1;
            final float maxX = ort == 5 ? value : 1;
            access.getBlock(x, y, z).setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
        }
    }

    public int getOrt(int meta) {
        switch (type) {
            default:
                return 1;
            case PILLAR:
                return getPillarOrt(meta);
            case ROTATABLE:
                return getRotatedOrt(meta);
        }
    }

    public enum Type {
        SIMPLE, CROSS, PILLAR, ROTATABLE
    }

    public enum Size {
        TINY, SMALL, MEDIUM, LARGE
    }

    public interface Getter {
        BlockTraitCommons getCommons();
    }
}
