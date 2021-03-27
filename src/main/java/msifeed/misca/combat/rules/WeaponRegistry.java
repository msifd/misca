package msifeed.misca.combat.rules;

import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.spell.Spell;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.ICaster;
import thaumcraft.common.items.casters.ItemFocus;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class WeaponRegistry {
    public HashMap<ResourceLocation, WeaponInfo> overrides = new HashMap<>();
    public WeaponInfoGeneration generation = new WeaponInfoGeneration();

    @Nonnull
    public WeaponInfo get(ItemStack stack) {
        if (stack.isEmpty()) return WeaponInfoGeneration.NONE;

        final Item item = stack.getItem();
        if (item instanceof ICaster)
            return getCasterInfo(stack, (ICaster) item);
        else if (item instanceof ISpellCastingItem)
            return getWandInfo(stack, (ISpellCastingItem) item);
        else
            return getItemInfo(item);
    }

    private WeaponInfo getWandInfo(ItemStack stack, ISpellCastingItem wand) {
        return getSpellInfo(wand.getCurrentSpell(stack));
    }

    public WeaponInfo getSpellInfo(Spell spell) {
        final WeaponInfo override = overrides.get(spell.getRegistryName());
        return generation.generateSpellInfo(spell, override);
    }

    public WeaponInfo getCasterInfo(ItemStack stack, ICaster caster) {
        final Item focus = caster.getFocus(stack);
        if (focus == null) return WeaponInfoGeneration.NONE;

        final FocusPackage core = ItemFocus.getPackage(caster.getFocusStack(stack));

        final WeaponInfo override = overrides.get(focus.getRegistryName());
        return generation.generateFocusInfo(core, override);
    }

    public WeaponInfo getItemInfo(Item item) {
        final WeaponInfo info = overrides.get(item.getRegistryName());
        return info != null ? info : generation.generateItemInfo(item);
    }
}
