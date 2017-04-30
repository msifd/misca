package ru.ariadna.misca.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import ru.ariadna.misca.Misca;

import java.util.Random;

class AriadnaDoor extends BlockDoor {

    static void register() {
        for (int i = 1; i <= 15; i++) {
            AriadnaDoor door = new AriadnaDoor(i);
            GameRegistry.registerBlock(door, "ariadna_door" + i);
            GameRegistry.registerItem(door.item, "ariadna_door_item" + i);
        }
    }

    private final AriadnaDoorItem item;

    private AriadnaDoor(int index) {
        super(Material.wood);
        item = new AriadnaDoorItem(index, this);

        disableStats();
        setHardness(3.0F);
        setStepSound(soundTypeWood);
        setBlockName("ariadna_door" + index);
        setBlockTextureName(Misca.MODID + ":ariadna_door" + index);
    }

    @Override
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_) {
        return item;
    }

    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return item;
    }
}
