package ru.ariadna.misca.things;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class MiscaThings {
    public static final CreativeTabs tab = new CreativeTabs("misca") {
        @Override
        public Item getTabIconItem() {
            return Items.bowl;
        }
    };

    @Subscribe
    public void onInit(FMLInitializationEvent event) {
        registerAriadnaDoors();
    }

    private void registerAriadnaDoors() {
        for (int i = 1; i <= 15; i++) {
            BlockAriadnaDoor door = new BlockAriadnaDoor(i);
            GameRegistry.registerBlock(door, "ariadna_door" + i);
            GameRegistry.registerItem(door.item, "ariadna_door_item" + i);
        }
    }
}
