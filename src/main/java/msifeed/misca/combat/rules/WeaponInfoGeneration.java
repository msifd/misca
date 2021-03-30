package msifeed.misca.combat.rules;

import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
    public double wizardryContinuousSpellOverheadFactor = 0;

    public WeaponInfo generateSpellInfo(EntityLivingBase caster, ItemStack stack, Spell spell, WeaponInfo override) {
        final WeaponInfo info = new WeaponInfo(WeaponTrait.range, WeaponTrait.magic);

        final double apPerMana = wizardryApPerSpellCost + override.atk;
        double factor = 1 + spell.getTier().level * wizardrySpellTierFactor;

        if (spell.isContinuous) {
            info.overhead = wizardryContinuousSpellOverheadFactor;
        }

        final int mana = getManaCost(caster, stack, spell);
        info.atk = mana * apPerMana * Math.max(0, factor);
        info.forceAtk = true;

        return info;
    }

    private int getManaCost(EntityLivingBase caster, ItemStack stack, Spell spell) {
        if (!(stack.getItem() instanceof ItemWand) || !(caster instanceof EntityPlayer))
            return spell.getCost();

        final ItemWand wand = (ItemWand) stack.getItem();
        final SpellModifiers modifiers = wand.calculateModifiers(stack, (EntityPlayer) caster, spell);

        final int cost = (int) (spell.getCost() * modifiers.get("cost") + 0.1F);
        if (!spell.isContinuous)
            return cost;

        final int castingTick = Math.max(0, caster.getItemInUseCount());
        if (castingTick % 20 == 0) {
            return cost / 2 + cost % 2;
        } else if (castingTick % 10 == 0) {
            return cost / 2;
        } else {
            return 0;
        }
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
        final double drawSpeedRate = core.getDrawTime() / speed;

        if (core instanceof CrossBow) {
            info.atk = tinkerCrossbowApPerShot + override.atk;

            if (!((CrossBow) core).isLoaded(stack)) {
                final double cost = tinkerCrossbowApPerDrawTime + override.use;
                info.use = cost * drawSpeedRate;
                info.atk = info.use; // For display purposes
            }
        } else {
            final double cost = tinkerBowApPerDrawTime + override.atk;
            info.atk = cost * drawSpeedRate;
        }

        return info;
    }
}
