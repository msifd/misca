package msifeed.misca.combat.rules;

import electroblob.wizardry.spell.Spell;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import slimeknights.tconstruct.library.tools.ProjectileLauncherNBT;
import slimeknights.tconstruct.library.tools.ranged.BowCore;
import slimeknights.tconstruct.tools.ranged.item.CrossBow;
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
    public double wizardryContinuousSpellApFactor = -0.8;
    public double wizardryContinuousSpellOverheadFactor = 0;

    public WeaponInfo generateSpellInfo(Spell spell, WeaponInfo override) {
        final WeaponInfo info = new WeaponInfo(WeaponTrait.range, WeaponTrait.magic);

        final double cost = wizardryApPerSpellCost + override.atk;
        double factor = 1 + spell.getTier().level * wizardrySpellTierFactor;

        if (spell.isContinuous) {
            factor += wizardryContinuousSpellApFactor;
            info.overhead = wizardryContinuousSpellOverheadFactor;
        }

        info.atk = spell.getCost() * cost * Math.max(0.01, factor);

        return info;
    }

    public double thaumcraftPowerCost = 0.3;
    public double thaumcraftComplexityFactor = 0.5;

    public WeaponInfo generateFocusInfo(FocusPackage core, WeaponInfo override) {
        final WeaponInfo info = new WeaponInfo(WeaponTrait.range, WeaponTrait.magic);

        final double cost = thaumcraftPowerCost + override.atk;
        final double factor = 1 + core.getComplexity() * thaumcraftComplexityFactor;
        info.atk = core.getPower() * cost * Math.max(0.01, factor);

        return info;
    }

    public double tinkerBowApPerDrawTime = 0.05;
    public double tinkerCrossbowApPerShot = 1;
    public double tinkerCrossbowApPerDrawTime = 0.05;

    public WeaponInfo generateTinkerBow(ItemStack stack, BowCore core, WeaponInfo override) {
        final WeaponInfo info = new WeaponInfo(WeaponTrait.range);

        final float speed = ProjectileLauncherNBT.from(stack).drawSpeed;
        final double drawTime = core.getDrawTime() / speed;

        if (core instanceof CrossBow) {
            info.atk = tinkerCrossbowApPerShot + override.atk;

            if (!((CrossBow) core).isLoaded(stack)) {
                final double cost = tinkerCrossbowApPerDrawTime + override.use;
                info.use = drawTime * cost;
                info.atk = info.use; // For display purposes
            }
        } else {
            final double cost = tinkerBowApPerDrawTime + override.atk;
            info.atk = drawTime * cost;
        }

        return info;
    }
}
