package msifeed.mc.misca.things;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.item.ItemSlab;
import net.minecraft.world.World;

import java.lang.reflect.Field;

public class RegularSlab extends BlockSlab {
    private final String parentName;
    private Item item;

    RegularSlab(Block parent, boolean isDouble) {
        super(isDouble, parent.getMaterial());
        this.parentName = parent.getUnlocalizedName().substring(5); // Remove `tile.`

        try {
            final Field f = Block.class.getDeclaredFields()[2];
            f.setAccessible(true);
            final String textureName = (String) f.get(parent);
            setBlockTextureName(textureName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setBlockName(getName());
        setStepSound(parent.stepSound);
        if (!isDouble)
            setCreativeTab(MiscaThings.blocksTab);
    }

    @Override
    public String func_150002_b(int p_150002_1_) {
        return getUnlocalizedName();
    }

    public String getName() {
        return parentName + (field_150004_a ? "_doubleslab" : "_slab");
    }

    @Override
    public net.minecraft.item.Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_) {
        return item;
    }

    private void setItem(Item item) {
        this.item = item;
    }

    public static class Item extends ItemSlab {
        public Item(Block block, RegularSlab halfSlab, RegularSlab doubleSlab) {
            super(block, halfSlab, doubleSlab, false);

            if (block instanceof RegularSlab)
                ((RegularSlab) block).setItem(this);

            setUnlocalizedName(halfSlab.getName() + "_item");
        }
    }
}
