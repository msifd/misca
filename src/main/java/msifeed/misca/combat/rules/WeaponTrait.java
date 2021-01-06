package msifeed.misca.combat.rules;

import msifeed.misca.combat.Combat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.Set;

public enum WeaponTrait {
    melee, range,
    evadeMelee, evadeRange
    ;

    public static Set<WeaponTrait> get(EntityLivingBase entity) {
        return Combat.getConfig().getWeaponOverride(entity)
                .map(wo -> wo.traits)
                .orElseGet(() -> {
                    final Item item = entity.getHeldItemMainhand().getItem();
                    final WeaponTrait type = item == Items.BOW ? range : melee;
                    return Collections.singleton(type);
                });
    }

    public static Set<WeaponTrait> get(DamageSource damageSource, EntityLivingBase entity) {
        final WeaponTrait typeFromDamage = damageSource instanceof EntityDamageSourceIndirect ? range : melee;
        return Combat.getConfig().getWeaponOverride(entity)
                .map(wo -> wo.traits)
                .orElse(Collections.singleton(typeFromDamage));
    }
}
