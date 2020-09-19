package msifeed.misca.genesis.blocks;

import msifeed.misca.Misca;
import msifeed.misca.genesis.blocks.templates.BlockTemplate;
import msifeed.misca.genesis.rules.IGenesisRule;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Objects;

public class BlockRule implements IGenesisRule {
    public String id;
    public String title;

    public boolean generateItemBlock = true;

    @Override
    public void generate() {
        BlockTemplate block = new BlockTemplate(this);
        block.setRegistryName(Misca.MODID, id);
        block.setUnlocalizedName(id);
        block.setCreativeTab(CreativeTabs.TRANSPORTATION);

        ForgeRegistries.BLOCKS.register(block);

        if (generateItemBlock)
            generateBlockItem(block);
    }

    private void generateBlockItem(Block block) {
        final ItemBlock itemBlock = new ItemBlock(block);
        itemBlock.setUnlocalizedName(id);

        itemBlock.setRegistryName(Objects.requireNonNull(block.getRegistryName()));
        ForgeRegistries.ITEMS.register(itemBlock);
    }
}
