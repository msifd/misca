package msifeed.misca.combat.rules;

import msifeed.misca.combat.Combat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;

import java.util.Collections;
import java.util.Set;

public enum WeaponTrait {
    melee, range, magic;

    public static Set<WeaponTrait> get(EntityLivingBase entity) {
        return Combat.getConfig().getWeaponOverride(entity)
                .map(wo -> wo.traits)
                .orElse(Collections.singleton(melee));
    }

    public static Set<WeaponTrait> get(DamageSource damageSource, EntityLivingBase entity) {
        final WeaponTrait typeFromDamage = damageSource instanceof EntityDamageSourceIndirect ? range : melee;
        return Combat.getConfig().getWeaponOverride(entity)
                .map(wo -> wo.traits)
                .orElse(Collections.singleton(typeFromDamage));
    }
}
