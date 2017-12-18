package msifeed.mc.misca.things.items;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import java.util.stream.IntStream;

public class Miscellaneous {

    public void onInit(FMLInitializationEvent event, CreativeTabs tab) {

        IntStream.range(1, 76).forEach(i -> {
            String itemName = "misc_" + i;
            GameRegistry.registerItem(new Item()
                            .setUnlocalizedName(itemName)
                            .setTextureName("misca:" + itemName)
                            .setCreativeTab(tab)
                    , itemName);
        });

    }

}
