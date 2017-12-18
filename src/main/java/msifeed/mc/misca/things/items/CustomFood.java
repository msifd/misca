package msifeed.mc.misca.things.items;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;

import java.util.stream.IntStream;

public class CustomFood {

    public CustomFood(CreativeTabs tab) {
        IntStream.range(1, 76).forEach(i -> {
            final String itemName = "food_" + i;
            final Item item = new ItemFood(0, 0.0F, false)
                    .setUnlocalizedName(itemName)
                    .setTextureName("misca:" + itemName)
                    .setCreativeTab(tab);
            GameRegistry.registerItem(item, itemName);
        });
    }

}
