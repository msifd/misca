package msifeed.mc.aorta.locks;

import cpw.mods.fml.common.registry.GameRegistry;
import msifeed.mc.aorta.locks.items.BlankKeyItem;
import msifeed.mc.aorta.locks.items.KeyItem;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class CopyKeyRecipe implements IRecipe {
    private final Item key = GameRegistry.findItem(Locks.MODID, KeyItem.ID);
    private final Item blank_key = GameRegistry.findItem(Locks.MODID, BlankKeyItem.ID);

    @Override
    public boolean matches(InventoryCrafting crafting, World world) {
        return doLookup(crafting) != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting crafting) {
        final Lookup l = doLookup(crafting);
        if (l == null)
            return null;
        final ItemStack s = l.key.copy();
        s.stackSize = l.blanks + 1;
        return s;
    }

    @Override
    public int getRecipeSize() {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

    private Lookup doLookup(InventoryCrafting crafting) {
        final Lookup lookup = new Lookup();

        for (int i = 0; i < crafting.getSizeInventory(); ++i) {
            final ItemStack stackInSlot = crafting.getStackInSlot(i);
            if (stackInSlot == null)
                continue;

            final Item itemInSlot = stackInSlot.getItem();
            if (itemInSlot == key) {
                if (lookup.key != null)
                    return null;
                lookup.key = stackInSlot;
            } else if (itemInSlot == blank_key) {
                lookup.blanks += 1;
            } else {
                return null;
            }
        }

        if (lookup.key == null || lookup.blanks == 0)
            return null;

        return lookup;
    }

    private static class Lookup {
        ItemStack key;
        int blanks;
    }
}
