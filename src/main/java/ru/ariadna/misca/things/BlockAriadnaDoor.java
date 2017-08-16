package ru.ariadna.misca.things;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import java.util.Random;

class BlockAriadnaDoor extends BlockDoor {
    final ItemAriadnaDoor item;

    BlockAriadnaDoor(String id_base, int index, Material material) {
        super(material);
        item = new ItemAriadnaDoor(id_base, index, this);

        disableStats();
        setHardness(3);
        setResistance(10);
        setStepSound(material == Material.iron ? soundTypeMetal : soundTypeWood);
        setBlockName(id_base + index);
        setBlockTextureName("misca:" + id_base + index);
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
