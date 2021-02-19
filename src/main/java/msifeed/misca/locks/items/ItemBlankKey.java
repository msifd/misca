package msifeed.misca.locks.items;

import msifeed.misca.Misca;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemBlankKey extends Item {
    public static final String ID = "blank_key";

    public ItemBlankKey() {
        setRegistryName(Misca.MODID, ID);
        setTranslationKey(ID);
        setCreativeTab(CreativeTabs.TOOLS);
    }
}
