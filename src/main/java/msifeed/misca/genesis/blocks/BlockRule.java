package msifeed.misca.genesis.blocks;

import msifeed.misca.Misca;
import msifeed.misca.genesis.blocks.templates.BlockTemplate;
import msifeed.misca.genesis.rules.IGenesisRule;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

public class BlockRule implements IGenesisRule {
    public String modId = Misca.MODID;
    public String id;

    public boolean generateItemBlock = true;

    @Override
    public void generate() {
        BlockTemplate block = new BlockTemplate(this);
        block.setRegistryName(modId, id);
        block.setUnlocalizedName(id);
        block.setCreativeTab(CreativeTabs.TRANSPORTATION);

        ForgeRegistries.BLOCKS.register(block);

        if (generateItemBlock)
            generateBlockItem(block);
    }

    private void generateBlockItem(Block block) {
        final ItemBlock itemBlock = new ItemBlock(block);
        itemBlock.setUnlocalizedName(block.getUnlocalizedName());

        itemBlock.setRegistryName(Objects.requireNonNull(block.getRegistryName()));
        ForgeRegistries.ITEMS.register(itemBlock);

        if (FMLCommonHandler.instance().getSide().isClient()) {
            registerModels(itemBlock);
        }
    }

    @SideOnly(Side.CLIENT)
    private void registerModels(ItemBlock itemBlock) {
        ModelLoader.setCustomModelResourceLocation(itemBlock, 0,
                new ModelResourceLocation(itemBlock.getRegistryName(), "inventory"));
    }
}
