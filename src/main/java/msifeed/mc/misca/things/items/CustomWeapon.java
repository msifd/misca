package msifeed.mc.misca.things.items;

import msifeed.mc.misca.things.items.ItemCustomWeapon;
import cpw.mods.fml.common.event.FMLInitializationEvent;
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

    public void onInit(FMLInitializationEvent event, CreativeTabs tab) {

        IntStream.range(1, 6).forEach(i -> {
            String itemName = "weapon_type1_" + i;
            GameRegistry.registerItem(new ItemCustomWeapon(weaponType1, itemName,  "misca:" + itemName, tab), itemName);
        });

        IntStream.range(1, 6).forEach(i -> {
            String itemName = "weapon_type2_" + i;
            GameRegistry.registerItem(new ItemCustomWeapon(weaponType2, itemName, "misca:" + itemName, tab), itemName);
        });

        IntStream.range(1, 6).forEach(i -> {
            String itemName = "weapon_type3_" + i;
            GameRegistry.registerItem(new ItemCustomWeapon(weaponType3, itemName, "misca:" + itemName, tab), itemName);
        });

        IntStream.range(1, 6).forEach(i -> {
            String itemName = "weapon_type4_" + i;
            GameRegistry.registerItem(new ItemCustomWeapon(weaponType4, itemName, "misca:" + itemName, tab), itemName);
        });

        IntStream.range(1, 6).forEach(i -> {
            String itemName = "weapon_type5_" + i;
            GameRegistry.registerItem(new ItemCustomWeapon(weaponType5, itemName, "misca:" + itemName, tab), itemName);
        });

    }

}

