package msifeed.mc.misca.things.items;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.util.EnumHelper;

import java.util.stream.IntStream;

public class CustomWeapon {

    public static final Item.ToolMaterial weaponType1 = EnumHelper.addToolMaterial("weaponMaterial1", 0, -1, 1.0F, 1.0F, 10);
    public static final Item.ToolMaterial weaponType2 = EnumHelper.addToolMaterial("weaponMaterial2", 0, -1, 1.0F, 2.0F, 10);
    public static final Item.ToolMaterial weaponType3 = EnumHelper.addToolMaterial("weaponMaterial3", 0, -1, 1.0F, 3.0F, 10);
    public static final Item.ToolMaterial weaponType4 = EnumHelper.addToolMaterial("weaponMaterial4", 0, -1, 1.0F, 4.0F, 10);
    public static final Item.ToolMaterial weaponType5 = EnumHelper.addToolMaterial("weaponMaterial5", 0, -1, 1.0F, 5.0F, 10);

    public CustomWeapon(CreativeTabs tab) {
        IntStream.range(1, 6).forEach(i -> {
            final String itemName = "weapon_type1_" + i;
            final ItemCustomWeapon item = new ItemCustomWeapon(weaponType1, itemName, "misca:" + itemName, tab);
            GameRegistry.registerItem(item, itemName);
        });

        IntStream.range(1, 6).forEach(i -> {
            final String itemName = "weapon_type2_" + i;
            final ItemCustomWeapon item = new ItemCustomWeapon(weaponType2, itemName, "misca:" + itemName, tab);
            GameRegistry.registerItem(item, itemName);
        });

        IntStream.range(1, 6).forEach(i -> {
            final String itemName = "weapon_type3_" + i;
            final ItemCustomWeapon item = new ItemCustomWeapon(weaponType3, itemName, "misca:" + itemName, tab);
            GameRegistry.registerItem(item, itemName);
        });

        IntStream.range(1, 6).forEach(i -> {
            final String itemName = "weapon_type4_" + i;
            final ItemCustomWeapon item = new ItemCustomWeapon(weaponType4, itemName, "misca:" + itemName, tab);
            GameRegistry.registerItem(item, itemName);
        });

        IntStream.range(1, 6).forEach(i -> {
            final String itemName = "weapon_type5_" + i;
            final ItemCustomWeapon item = new ItemCustomWeapon(weaponType5, itemName, "misca:" + itemName, tab);
            GameRegistry.registerItem(item, itemName);
        });
    }

}

