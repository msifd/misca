package msifeed.mc.aorta.genesis.items;

import com.google.gson.JsonObject;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import msifeed.mc.aorta.genesis.GenesisCreativeTab;
import msifeed.mc.aorta.genesis.Generator;
import msifeed.mc.aorta.genesis.GenesisTrait;
import msifeed.mc.aorta.genesis.items.data.ArmorData;
import msifeed.mc.aorta.genesis.items.data.BookData;
import msifeed.mc.aorta.genesis.items.templates.ArmorTemplate;
import msifeed.mc.aorta.genesis.items.templates.BookTemplate;
import msifeed.mc.aorta.genesis.items.templates.FoodTemplate;
import msifeed.mc.aorta.genesis.items.templates.ItemTemplate;
import net.minecraft.item.Item;

import java.util.HashSet;

import static msifeed.mc.aorta.genesis.GenesisTrait.*;

public class ItemGenerator implements Generator {
    @Override
    public void init() {

    }

    @Override
    public void generate(JsonObject json, HashSet<GenesisTrait> traits) {
        final ItemGenesisUnit unit = new ItemGenesisUnit(json, traits);
        final Item item = getItemTemplate(json, unit);
        fillCommons(unit, item);
        GameRegistry.registerItem(item, unit.id);
    }

    private Item getItemTemplate(JsonObject json, ItemGenesisUnit unit) {
        if (unit.hasTrait(armor))
            return new ArmorTemplate(unit, new ArmorData(json));
        if (unit.hasTrait(consumable))
            return new FoodTemplate(unit);
        if (unit.hasTrait(book))
            return new BookTemplate(unit, new BookData(json));
        return new ItemTemplate(unit);
    }

    private void fillCommons(ItemGenesisUnit unit, Item item) {
        item.setCreativeTab(GenesisCreativeTab.ITEMS);

        if (unit.hasTrait(not_stackable))
            item.setMaxStackSize(1);
        if (unit.hasTrait(hold_like_tool))
            item.setFull3D();

        if (FMLCommonHandler.instance().getSide().isClient()) {
            ClientGenerator.fillTexture(unit, item);
        }
    }
}
