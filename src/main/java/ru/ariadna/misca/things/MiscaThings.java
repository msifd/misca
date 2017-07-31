package ru.ariadna.misca.things;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.material.Material;
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
        GameRegistry.registerTileEntity(RegularChest.ChestEntity.class, "misca.ariadna_chest");

        for (int i = 1; i <= 25; i++) {
            final String id_base = "ariadna_door";
            BlockAriadnaDoor door = new BlockAriadnaDoor(id_base, i, Material.wood);
            GameRegistry.registerBlock(door, id_base + i);
            GameRegistry.registerItem(door.item, id_base + "_item" + i);
        }

        for (int i = 1; i <= 10; i++) {
            final String id_base = "ariadna_iron_door";
            BlockAriadnaDoor door = new BlockAriadnaDoor(id_base, i, Material.iron);
            GameRegistry.registerBlock(door, id_base + i);
            GameRegistry.registerItem(door.item, id_base + "_item" + i);
        }

        for (int i = 1; i <= 50; i++) {
            GameRegistry.registerBlock(new RegularBlock(i), RegularBlock.NAME_BASE + i);
        }

        for (int i = 1; i <= 25; i++) {
            GameRegistry.registerBlock(new RegularPillar(i), RegularPillar.NAME_BASE + i);
        }

        for (int i = 1; i <= 25; i++) {
            GameRegistry.registerBlock(new RegularBarrel(i), RegularBarrel.NAME_BASE + i);
        }

        for (int i = 1; i <= 10; i++) {
            String name_base = "misca_torch_";
            GameRegistry.registerBlock(new RegularTorch(name_base, i).setLightLevel(0.9375F), name_base + i);
        }

        for (int i = 1; i <= 10; i++) {
            String name_base = "misca_candle_";
            GameRegistry.registerBlock(new RegularTorch(name_base, i).setLightLevel(0.5F), name_base + i);
        }

        for (int i = 1; i <= 10; i++) {
            GameRegistry.registerBlock(new RegularPane(i), RegularPane.NAME_BASE + i);
        }

        for (int i = 1; i <= 5; i++) {
            GameRegistry.registerBlock(new RegularBrewingStand(i), RegularBrewingStand.NAME_BASE + i);
        }

        for (int i = 1; i <= 20; i++) {
            GameRegistry.registerBlock(new RegularChest(i), RegularChest.NAME_BASE + i);
        }

        GameRegistry.registerBlock(new TransparentBlock(), TransparentBlock.NAME_BASE);
    }
}
