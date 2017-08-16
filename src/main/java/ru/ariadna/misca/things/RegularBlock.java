package ru.ariadna.misca.things;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class RegularBlock extends Block {
    static final String NAME_BASE = "misca_block_";

    RegularBlock(int index) {
        super(Material.rock);

        setBlockName(NAME_BASE + index);
        setBlockTextureName("misca:" + NAME_BASE + index);
        setCreativeTab(MiscaThings.tab);

        setHardness(2);
        setResistance(10);
        setStepSound(soundTypePiston);
    }
}
