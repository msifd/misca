package msifeed.misca.combat.rules;

import msifeed.misca.combat.CharAttribute;
import msifeed.misca.combat.cap.CombatantProvider;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import java.util.EnumMap;
import java.util.EnumSet;

public class CombatantInfo {
    public final Vec3d pos;
    public final EnumMap<CharAttribute, Double> attributes = new EnumMap<>(CharAttribute.class);
    private final WeaponTrait mainType;
    private final EnumSet<WeaponTrait> traitsMain;
    private final EnumSet<WeaponTrait> traitsOff;

    public CombatantInfo(EntityLivingBase attacker, DamageSource source, ResourceLocation weapon) {
        this.pos = attacker.getPositionVector();

        final WeaponTrait defaultType = source instanceof EntityDamageSourceIndirect ? WeaponTrait.range : WeaponTrait.melee;
        this.mainType = getMainType(weapon, defaultType);
        this.traitsMain = WeaponRegistry.getWeaponInfo(weapon).traits;
        this.traitsOff = WeaponRegistry.getWeaponInfo(attacker.getHeldItemOffhand().getItem().getRegistryName()).traits;

        for (CharAttribute attr : CharAttribute.values())
            attributes.put(attr, attr.get(attacker));
    }

    public CombatantInfo(EntityLivingBase victim) {
        this.pos = CombatantProvider.get(victim).getPosition();

        this.mainType = getMainType(victim.getHeldItemMainhand().getItem().getRegistryName(), WeaponTrait.melee);
        this.traitsMain = WeaponRegistry.getWeaponInfo(victim.getHeldItemMainhand().getItem().getRegistryName()).traits;
        this.traitsOff = WeaponRegistry.getWeaponInfo(victim.getHeldItemOffhand().getItem().getRegistryName()).traits;

        for (CharAttribute attr : CharAttribute.values())
            attributes.put(attr, attr.get(victim));
    }

    private WeaponTrait getMainType(ResourceLocation weapon, WeaponTrait defaultType) {
        final WeaponInfo info = WeaponRegistry.getWeaponInfo(weapon);

        if (info == WeaponInfo.NONE) return defaultType;
        if (info.traits.contains(WeaponTrait.range)) return WeaponTrait.range;
        return defaultType;
    }

    public boolean isRanged() {
        return mainType == WeaponTrait.range;
    }

    public boolean isMelee() {
        return !isRanged();
    }

    public boolean is(WeaponTrait trait) {
        return traitsMain.contains(trait);
    }

    public boolean isAny(WeaponTrait trait) {
        return traitsMain.contains(trait) || traitsOff.contains(trait);
    }
}
