package ru.ariadna.misca.things;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class MiscaThings {
    public static final MiscaCreativeTab tab = new MiscaCreativeTab();

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

        for (int i = 1; i <= 65; i++) {
            String name_base = "misca_block_";
            RegularBlock block = new RegularBlock(name_base, i);
            block.setResistance(10);
            GameRegistry.registerBlock(block, name_base + i);
        }

        for (int i = 1; i <= 4; i++) {
            String name_base = "misca_block_ub_";
            RegularBlock block = new RegularBlock(name_base, i);
            block.setBlockUnbreakable().setResistance(6000000.0F);
            GameRegistry.registerBlock(block, name_base + i);
        }

        for (int i = 1; i <= 30; i++) {
            String name_base = "misca_block_wool_";
            RegularBlock block = new RegularBlock(name_base, i);
            block.setStepSound(Block.soundTypeCloth).setHardness(0.8F);
            GameRegistry.registerBlock(block, name_base + i);
        }

        for (int i = 1; i <= 40; i++) {
            GameRegistry.registerBlock(new RegularPillar(i), RegularPillar.NAME_BASE + i);
        }

        for (int i = 1; i <= 40; i++) {
            String name_base = "misca_barrel_";
            GameRegistry.registerBlock(new RegularBarrel(name_base, i), name_base + i);
        }

        for (int i = 1; i <= 4; i++) {
            String name_base = "misca_barrel_ub_";
            RegularBarrel block = new RegularBarrel(name_base, i);
            block.setBlockUnbreakable().setResistance(6000000.0F);
            GameRegistry.registerBlock(block, name_base + i);
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

        for (int i = 1; i <= 25; i++) {
            GameRegistry.registerBlock(new RegularCarpet(i), RegularCarpet.NAME_BASE + i);
        }

        for (int i = 1; i <= 5; i++) {
            GameRegistry.registerBlock(new RegularBrewingStand(i), RegularBrewingStand.NAME_BASE + i);
        }

        for (int i = 1; i <= 20; i++) {
            GameRegistry.registerBlock(new RegularChest(i), RegularChest.NAME_BASE + i);
        }

        for (int i = 1; i <= 15; i++) {
            String name_base = "misca_cross_";
            RegularCross block = new RegularCross(name_base, i);
            block.setHardness(4);
            GameRegistry.registerBlock(block, name_base + i);
        }

        for (int i = 1; i <= 15; i++) {
            String name_base = "misca_pad_";
            RegularPad block = new RegularPad(name_base, i);
            block.setHardness(4);
            GameRegistry.registerBlock(block, name_base + i);
        }

        for (int i = 1; i <= 15; i++) {
            final String id_base = "misca_bed_";
            RegularBed block = new RegularBed(id_base, i);
            GameRegistry.registerBlock(block, id_base + i);
            GameRegistry.registerItem(block.item, id_base + "_item" + i);
        }

        for (int i = 1; i <= 10; i++) {
            final String id_base = "misca_trapdoor_";
            RegularTrapdoor block = new RegularTrapdoor(id_base, i);
            GameRegistry.registerBlock(block, id_base + i);
        }

        GameRegistry.registerBlock(new TransparentBlock(), TransparentBlock.NAME_BASE);
    }
}
