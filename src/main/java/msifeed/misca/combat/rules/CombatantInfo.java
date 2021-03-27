package msifeed.misca.combat.rules;

import msifeed.misca.combat.CharAttribute;
import msifeed.misca.combat.Combat;
import msifeed.misca.combat.cap.CombatantProvider;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.math.Vec3d;

import java.util.EnumMap;
import java.util.EnumSet;

public class CombatantInfo {
    public final Vec3d pos;
    public final EnumMap<CharAttribute, Double> attributes = new EnumMap<>(CharAttribute.class);

    private final WeaponInfo weapon;
    private final WeaponTrait mainType;
    private final EnumSet<WeaponTrait> traitsMain;
    private final EnumSet<WeaponTrait> traitsOff;

    public CombatantInfo(EntityLivingBase attacker, DamageSource source, WeaponInfo weapon) {
        this.pos = attacker.getPositionVector();

        this.weapon = weapon;
        final WeaponTrait defaultType = source instanceof EntityDamageSourceIndirect ? WeaponTrait.range : WeaponTrait.melee;
        this.mainType = getMainType(weapon, defaultType);
        this.traitsMain = weapon.traits;
        this.traitsOff = Combat.getWeapons().get(attacker.getHeldItemOffhand()).traits;

        for (CharAttribute attr : CharAttribute.values())
            attributes.put(attr, attr.get(attacker));
    }

    public CombatantInfo(EntityLivingBase victim) {
        this.pos = CombatantProvider.get(victim).getPosition();

        this.weapon = Combat.getWeapons().get(victim.getHeldItemMainhand());
        this.mainType = getMainType(weapon, WeaponTrait.melee);
        this.traitsMain = weapon.traits;
        this.traitsOff = Combat.getWeapons().get(victim.getHeldItemOffhand()).traits;

        for (CharAttribute attr : CharAttribute.values())
            attributes.put(attr, attr.get(victim));
    }

    private WeaponTrait getMainType(WeaponInfo info, WeaponTrait defaultType) {
        if (info == WeaponInfoGeneration.NONE) return defaultType;
        if (info.traits.contains(WeaponTrait.range)) return WeaponTrait.range;
        return defaultType;
    }

    public boolean isRanged() {
        return mainType == WeaponTrait.range;
    }

    public boolean isMelee() {
        return mainType == WeaponTrait.melee;
    }

    public boolean is(WeaponTrait trait) {
        return traitsMain.contains(trait);
    }

    public boolean isAny(WeaponTrait trait) {
        return traitsMain.contains(trait) || traitsOff.contains(trait);
    }
}
