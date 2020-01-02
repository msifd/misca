package msifeed.mc.aorta.genesis;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import msifeed.mc.aorta.sys.utils.AlphanumComparator;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GenesisCreativeTab extends CreativeTabs {
    private static final Item iconBlocks = makeIcon("tab_blocks");
    public static final GenesisCreativeTab BLOCKS = new GenesisCreativeTab("aorta.blocks", iconBlocks);
    private static final Item iconItems = makeIcon("tab_items");
    public static final GenesisCreativeTab ITEMS = new GenesisCreativeTab("aorta.items", iconItems);
    private static final Item iconTools = makeIcon("tab_tools");
    public static final GenesisCreativeTab TOOLS = new GenesisCreativeTab("aorta.tools", iconTools);
    private static final Item iconLock = makeIcon("tab_locks");
    public static final GenesisCreativeTab LOCKS = new GenesisCreativeTab("aorta.locks", iconLock);

    private final Item icon;
    private ArrayList<ItemStack> cache = new ArrayList<>();

    private GenesisCreativeTab(String name, Item icon) {
        super(name);
        this.icon = icon;
    }

    public static void init() {
        // static init
    }

    private static Item makeIcon(String name) {
        final Item item = new Item();
        item.setTextureName("misca:" + name);
        GameRegistry.registerItem(item, name);
        return item;
    }

    @Override
    public Item getTabIconItem() {
        return icon;
    }

    @Override
    public boolean hasSearchBar() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public void displayAllReleventItems(List list) {
        cache.clear();
        for (Object anItemRegistry : Item.itemRegistry) {
            Item item = (Item) anItemRegistry;
            if (item == null) continue;

            for (CreativeTabs tab : item.getCreativeTabs()) {
                if (tab == this) {
                    item.getSubItems(item, this, cache);
                }
            }
        }

        cache.sort(Comparator.comparing(ItemStack::getUnlocalizedName, new AlphanumComparator()));
        list.addAll(cache);

        if (this.func_111225_m() != null) {
            this.addEnchantmentBooksToList(list, this.func_111225_m());
        }
    }
}
