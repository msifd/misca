package msifeed.mc.misca.things.items;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import java.util.stream.IntStream;

public class Miscellaneous {

    public Miscellaneous(CreativeTabs tab) {
        IntStream.range(1, 76).forEach(i -> {
            final String itemName = "misc_" + i;
            final Item item = new Item()
                    .setUnlocalizedName(itemName)
                    .setTextureName("misca:" + itemName)
                    .setCreativeTab(tab);
            GameRegistry.registerItem(item, itemName);
        });
    }

}
