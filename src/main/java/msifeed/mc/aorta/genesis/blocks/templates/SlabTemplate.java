package msifeed.mc.aorta.genesis.blocks.templates;

import msifeed.mc.aorta.genesis.blocks.BlockTraitCommons;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

public class SlabTemplate extends BlockSlab implements BlockTraitCommons.Getter {
    private final BlockTraitCommons traits;
    private final String selfId;
    private Item item;

    public SlabTemplate(Block parent, boolean isDouble, String id, BlockTraitCommons traits) {
        super(isDouble, parent.getMaterial());
        this.traits = traits;
        this.selfId = id;
        setBlockName(id);

        if (isDouble)
            item = Item.getItemFromBlock(parent); // Refer to parent block on middle click
    }

    @Override
    public BlockTraitCommons getCommons() {
        return traits;
    }

    @Override
    public String func_150002_b(int meta) {
        return getUnlocalizedName();
    }

    @Override
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_) {
        return item;
    }

    public static class SlabItem extends ItemSlab {
        public SlabItem(Block block, SlabTemplate halfSlab, SlabTemplate doubleSlab) {
            super(block, halfSlab, doubleSlab, false);

            if (block instanceof SlabTemplate)
                ((SlabTemplate) block).item = this;

            setUnlocalizedName(halfSlab.selfId + "_item");
        }

        @Override
        public void addInformation(ItemStack itemStack, EntityPlayer player, List lines, boolean debug) {
            if (!(field_150939_a instanceof BlockTraitCommons.Getter))
                return;

            final BlockTraitCommons commons = ((BlockTraitCommons.Getter) field_150939_a).getCommons();
            if (commons != null && commons.unit.description != null)
                Collections.addAll(lines, commons.unit.description);
        }
    }
}
