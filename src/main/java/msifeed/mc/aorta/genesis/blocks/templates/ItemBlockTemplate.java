package msifeed.mc.aorta.genesis.blocks.templates;

import msifeed.mc.aorta.genesis.blocks.BlockTraitCommons;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class ItemBlockTemplate extends ItemBlock  {
    public ItemBlockTemplate(Block block) {
        super(block);
    }

    @Override
    public String getItemStackDisplayName(ItemStack itemStack) {
        return BlockTraitCommons.getItemStackDisplayName(field_150939_a, itemStack);
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
