package ru.ariadna.misca.things;

import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;

public class RegularPane extends BlockPane {
    public static final String NAME_BASE = "misca_pane_";

    protected RegularPane(int index) {
        super("misca:" + NAME_BASE + index, "misca:" + NAME_BASE + index + "_top", Material.iron, true);

        setBlockName(NAME_BASE + index);
        setBlockTextureName("misca:" + NAME_BASE + index);
        setCreativeTab(MiscaThings.tab);

        setHardness(2);
        setResistance(10);
        setStepSound(soundTypeMetal);
    }
}
