package msifeed.misca.genesis.tabs;

import msifeed.misca.genesis.rules.IGenesisRule;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.core.util.Assert;

import java.util.Objects;

public class CreativeTabRule implements IGenesisRule {
    public String id;
    public String title;
    public String item;

    @Override
    public void generate() {
        Assert.requireNonEmpty(id);
        Assert.requireNonEmpty(title);
        Assert.requireNonEmpty(item);

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
