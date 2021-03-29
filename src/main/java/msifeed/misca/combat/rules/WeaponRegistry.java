package msifeed.misca.combat.rules;

import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.spell.Spell;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.tools.ranged.BowCore;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.ICaster;
import thaumcraft.common.items.casters.ItemFocus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
        else if (item instanceof BowCore)
            return getTinckerBow(stack);
        else
            return getItemInfo(item);
    }

    private WeaponInfo getWandInfo(ItemStack stack, ISpellCastingItem wand) {
        return getSpellInfo(wand.getCurrentSpell(stack));
    }

    public WeaponInfo getSpellInfo(Spell spell) {
        final WeaponInfo override = getOverride(spell.getRegistryName());
        return generation.generateSpellInfo(spell, override);
    }

    public WeaponInfo getCasterInfo(ItemStack stack, ICaster caster) {
        final Item focus = caster.getFocus(stack);
        if (focus == null) return WeaponInfoGeneration.NONE;

        final FocusPackage core = ItemFocus.getPackage(caster.getFocusStack(stack));

        final WeaponInfo override = getOverride(focus.getRegistryName());
        return generation.generateFocusInfo(core, override);
    }

    public WeaponInfo getTinckerBow(ItemStack stack) {
        final BowCore core = (BowCore) stack.getItem();
        final WeaponInfo override = getOverride(core.getRegistryName());
        return generation.generateTinkerBow(stack, core, override);
    }

    public WeaponInfo getItemInfo(Item item) {
        final WeaponInfo info = overrides.get(item.getRegistryName());
        return info != null ? info : generation.generateItemInfo(item);
    }

    @Nonnull
    private WeaponInfo getOverride(ResourceLocation key) {
        return overrides.getOrDefault(key, WeaponInfoGeneration.NONE);
    }
}
