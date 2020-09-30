package msifeed.misca.genesis.items;

import msifeed.misca.Misca;
import msifeed.misca.genesis.rules.IGenesisRule;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.stream.Stream;

public class ItemRule implements IGenesisRule {
    public String modId = Misca.MODID;
    public String id;
    public String tab = "";

    @Override
    public void generate() {
        final Item item = new ItemTemplate(this);
        item.setRegistryName(modId, id);
        item.setUnlocalizedName(id);

        ForgeRegistries.ITEMS.register(item);

        if (FMLCommonHandler.instance().getSide().isClient()) {
            if (!tab.isEmpty())
                item.setCreativeTab(findTab());

            registerModels(item);
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
    private void registerModels(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0,
                new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}
