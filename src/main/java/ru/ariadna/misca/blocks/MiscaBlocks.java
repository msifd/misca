package ru.ariadna.misca.blocks;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class MiscaBlocks {
    static final CreativeTabs tab = new CreativeTabs("misca") {
        @Override
        public Item getTabIconItem() {
            return Items.bowl;
        }
    };

    public static void register() {
        BlockAriadnaDoor.register();
    }
}
