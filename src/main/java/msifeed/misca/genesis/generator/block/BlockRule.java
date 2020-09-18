package msifeed.misca.genesis.generator.block;

import msifeed.misca.Misca;
import msifeed.misca.genesis.generator.block.templates.BlockTemplate;
import msifeed.misca.genesis.rules.IGenesisRule;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Objects;

public class BlockRule implements IGenesisRule {
    public String id;
    public String title;

    public boolean generateItemBlock = true;

    @Override
    public void generate() {
        BlockTemplate block = new BlockTemplate(this);
        block.setRegistryName(Misca.MODID, id);
        block.setCreativeTab(CreativeTabs.TRANSPORTATION);

        GameRegistry.findRegistry(Block.class).register(block);

        if (generateItemBlock)
            generateBlockItem(block);
    }

    private void generateBlockItem(Block block) {
        final ItemBlock itemBlock = new ItemBlock(block);
        itemBlock.setUnlocalizedName(title);

        itemBlock.setRegistryName(Objects.requireNonNull(block.getRegistryName()));
        GameRegistry.findRegistry(Item.class).register(itemBlock);
    }
}
