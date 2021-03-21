package msifeed.misca.combat.rules;

import electroblob.wizardry.spell.Spell;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class WeaponInfoGeneration {
    public static final WeaponInfo GENERIC_MELEE = new WeaponInfo(WeaponTrait.melee);
    public static final WeaponInfo GENERIC_SHIELD = new WeaponInfo(WeaponTrait.evadeMelee);
    public static final WeaponInfo GENERIC_RANGE = new WeaponInfo(WeaponTrait.range);
    public static final WeaponInfo GENERIC_FOOD = new WeaponInfo(WeaponTrait.canUse);

    public WeaponInfo generateInfo(IForgeRegistryEntry<?> weapon) {
        if (weapon instanceof Item) return getItemInfo((Item) weapon);
        if (weapon instanceof Spell) return getSpellInfo((Spell) weapon);
        else return WeaponInfo.NONE;
    }

    private WeaponInfo getItemInfo(Item item) {
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

        return WeaponInfo.NONE;
    }

    public double apPerSpellCost = 0.3;
    public double spellTierFactor = 0.5;
    public double continuousSpellFactor = -0.9;

    private WeaponInfo getSpellInfo(Spell spell) {
        final WeaponInfo info = new WeaponInfo(WeaponTrait.range);

        double atkFactor = 1 + spell.getTier().level * spellTierFactor;
        if (spell.isContinuous)
            atkFactor += continuousSpellFactor;

        info.atk = spell.getCost() * apPerSpellCost * atkFactor;

        return info;
    }
}
