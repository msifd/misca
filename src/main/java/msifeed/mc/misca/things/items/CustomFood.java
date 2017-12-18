package msifeed.mc.misca.things.items;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemFood;

import java.util.stream.IntStream;

public class CustomFood {

    public void onInit(FMLInitializationEvent event,  CreativeTabs tab) {

        IntStream.range(1, 76).forEach(i -> {
            String itemName = "food_" + i;
            GameRegistry.registerItem(new ItemFood(0, 0.0F, false)

                            .setUnlocalizedName(itemName)
                            .setTextureName("misca:" + itemName)
                            .setCreativeTab(tab)
                    , itemName);
        });

    }

}
