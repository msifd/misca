package msifeed.mc.misca.things.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemSword;

public class ItemCustomWeapon extends ItemSword {

    public ItemCustomWeapon(ToolMaterial material, String weaponName, String textureName, CreativeTabs tab) {
        super(material);

        setUnlocalizedName(weaponName);
        setTextureName(textureName);
        setCreativeTab(tab);
    }

}
