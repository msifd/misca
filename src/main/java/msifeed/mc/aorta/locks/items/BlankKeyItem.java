package msifeed.mc.aorta.locks.items;

import msifeed.mc.aorta.genesis.GenesisCreativeTab;
import msifeed.mc.aorta.locks.Locks;
import net.minecraft.item.Item;

public class BlankKeyItem extends Item {
    public static final String ID = "lock_blank_key";

    public BlankKeyItem() {
        setCreativeTab(GenesisCreativeTab.LOCKS);
        setUnlocalizedName(ID);
        setTextureName(Locks.MODID + ":" + ID);
    }
}
