package msifeed.misca.genesis.items;

import net.minecraft.item.Item;

public class ItemTemplate extends Item implements IItemTemplate {
    private final ItemRule rule;

    public ItemTemplate(ItemRule rule) {
        this.rule = rule;
    }

    @Override
    public ItemRule getRule() {
        return rule;
    }
}
