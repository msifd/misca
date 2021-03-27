package msifeed.misca.combat.rules;

import electroblob.wizardry.spell.Spell;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import thaumcraft.api.casters.FocusPackage;

public class WeaponInfoGeneration {
    public static final WeaponInfo NONE = new WeaponInfo();
    public static final WeaponInfo GENERIC_MELEE = new WeaponInfo(WeaponTrait.melee);
    public static final WeaponInfo GENERIC_SHIELD = new WeaponInfo(WeaponTrait.evadeMelee);
    public static final WeaponInfo GENERIC_RANGE = new WeaponInfo(WeaponTrait.range);
    public static final WeaponInfo GENERIC_FOOD = new WeaponInfo(WeaponTrait.canUse);

    public WeaponInfo generateItemInfo(Item item) {
        if (item instanceof ItemSword) return GENERIC_MELEE;

        switch (item.getItemUseAction(ItemStack.EMPTY)) {
            case EAT:
            case DRINK:
                return GENERIC_FOOD;
            case BLOCK:
                return GENERIC_SHIELD;
            case BOW:
                return GENERIC_RANGE;
        }

        return NONE;
    }

    public double wizardryApPerSpellCost = 0.3;
    public double wizardrySpellTierFactor = 0.5;
    public double wizardryContinuousSpellFactor = -0.9;

    public WeaponInfo generateSpellInfo(Spell spell, WeaponInfo override) {
        final WeaponInfo info = new WeaponInfo(WeaponTrait.range, WeaponTrait.magic);

        double factor = 1 + spell.getTier().level * wizardrySpellTierFactor;
        if (spell.isContinuous)
            factor += wizardryContinuousSpellFactor;

        info.atk = spell.getCost() * wizardryApPerSpellCost * factor;

        return info;
    }

    public double thaumcraftPowerCost = 0.3;
    public double thaumcraftComplexityFactor = 0.5;

    public WeaponInfo generateFocusInfo(FocusPackage core, WeaponInfo override) {
        final WeaponInfo info = new WeaponInfo(WeaponTrait.range, WeaponTrait.magic);

        final double cost = thaumcraftPowerCost + override.atk;
        final double factor = 1 + core.getComplexity() * thaumcraftComplexityFactor;
        info.atk = core.getPower() * cost * factor;

        return info;
    }
}
