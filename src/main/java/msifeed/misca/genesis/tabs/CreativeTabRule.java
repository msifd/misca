package msifeed.misca.genesis.tabs;

import msifeed.misca.genesis.rules.IGenesisRule;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.Validate;

import java.util.Objects;

public class CreativeTabRule implements IGenesisRule {
    public String id;
    public String title;
    public String item;

    @Override
    public void generate() {
        Validate.notEmpty(id, "Tab `id` is empty!");
        Validate.notEmpty(title, "Tab `title` is empty!");
        Validate.notEmpty(item, "Tab `item` is empty!");

        new CreativeTabs(id) {
            @SideOnly(Side.CLIENT)
            @Override
            public ItemStack getTabIconItem() {
                return new ItemStack(Objects.requireNonNull(Item.getByNameOrId(item)));
            }

            @Override
            public String getTranslatedTabLabel() {
                return title;
            }
        };
    }
}
