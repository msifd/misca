package msifeed.mc.aorta.genesis.content;

import cpw.mods.fml.common.registry.GameRegistry;
import msifeed.mc.aorta.genesis.Genesis;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSign;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.Random;

public class EmptySignBlock extends BlockSign {
    static final Block standing_empty_sign = new EmptySignBlock(true);
    static final Block wall_empty_sign = new EmptySignBlock(false);
    static final Item empty_sign_item = new EmptySignItem();

    EmptySignBlock(boolean isStanding) {
        super(TileEntitySign.class, isStanding);
        setBlockName("empty_sign");
        setBlockTextureName(Genesis.MODID + ":empty");
    }

    public static void register() {
        GameRegistry.registerBlock(standing_empty_sign, "standing_empty_sign");
        GameRegistry.registerBlock(wall_empty_sign, "wall_empty_sign");
        GameRegistry.registerItem(empty_sign_item, "empty_sign");
        GameRegistry.registerTileEntity(Tile.class, "empty_sign_te");
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new Tile();
    }

    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return empty_sign_item;
    }

    @Override
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_) {
        return empty_sign_item;
    }

    @Override
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        return blockIcon;
    }

    public static class Tile extends TileEntitySign {
    }
}
