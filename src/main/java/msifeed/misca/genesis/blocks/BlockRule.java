package msifeed.misca.genesis.blocks;

import msifeed.misca.Misca;
import msifeed.misca.genesis.rules.IGenesisRule;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.Validate;

import java.util.Objects;
import java.util.stream.Stream;

public class BlockRule implements IGenesisRule {
    public String modId = Misca.MODID;
    public String id;
    public BlockType type = BlockType.plain;
    public String tab = "";

    public float resistance = 1;
    public float hardness = 1;
    public float slipperiness = 0.6f;
    public int lightOpacity = 0;
    public float lightLevel = 0;

    public int containerCapacity = 9;

    public boolean generateItemBlock = true;

    @Override
    public void generate() {
        Validate.notEmpty(modId, "Block `modId` is empty!");
        Validate.notEmpty(id, "Block `id` is empty!");

        final Block block = type.createBlock(this);

        block.setRegistryName(modId, id);
        block.setUnlocalizedName(id);

        // TODO: material
        // TODO: sound
        // TODO: block state
        // TODO: sub blocks

        block.setResistance(resistance);
        block.setHardness(hardness);
        block.setDefaultSlipperiness(slipperiness);

        block.setLightLevel(lightLevel);
        block.setLightOpacity(lightOpacity);

        if (FMLCommonHandler.instance().getSide().isClient()) {
            if (!tab.isEmpty())
                block.setCreativeTab(findTab());
        }

        ForgeRegistries.BLOCKS.register(block);

        if (generateItemBlock) {
            generateBlockItem(block);
        }
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
    private CreativeTabs findTab() {
        return Stream.of(CreativeTabs.CREATIVE_TAB_ARRAY)
                .filter(t -> t.getTabLabel().equals(tab))
                .findAny()
                .orElseThrow(() -> new RuntimeException("unknown tab: " + tab));
    }

    @SideOnly(Side.CLIENT)
    private void registerModels(ItemBlock itemBlock) {
        ModelLoader.setCustomModelResourceLocation(itemBlock, 0,
                new ModelResourceLocation(Objects.requireNonNull(itemBlock.getRegistryName()), "inventory"));
    }
}
