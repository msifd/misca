package msifeed.misca.combat.rules;

import msifeed.misca.charsheet.CharAttribute;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.combat.Combat;
import msifeed.misca.combat.cap.CombatantProvider;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

import java.util.EnumMap;
import java.util.EnumSet;

public class CombatantInfo {
    public final Vec3d pos;
    public final EnumMap<CharAttribute, Double> attributes = new EnumMap<>(CharAttribute.class);
    private final EnumSet<WeaponTrait> traitsMain;
    private final EnumSet<WeaponTrait> traitsOff;

    public CombatantInfo(EntityLivingBase attacker, DamageSource source) {
        this.pos = attacker.getPositionVector();

        final WeaponTrait typeFromDamage = source instanceof EntityDamageSourceIndirect ? WeaponTrait.range : WeaponTrait.melee;
        this.traitsMain = Combat.getWeaponInfo(attacker, EnumHand.MAIN_HAND)
                .map(wo -> wo.traits)
                .orElse(EnumSet.of(typeFromDamage));
        this.traitsOff = Combat.getWeaponInfo(attacker, EnumHand.OFF_HAND)
                .map(wo -> wo.traits)
                .orElse(EnumSet.noneOf(WeaponTrait.class));

        for (CharAttribute attr : CharAttribute.values())
            attributes.put(attr, attr.get(attacker));
    }

    public CombatantInfo(EntityLivingBase victim) {
        this.pos = CombatantProvider.get(victim).getPosition();

        this.traitsMain = Combat.getWeaponInfo(victim, EnumHand.MAIN_HAND)
                .map(wo -> wo.traits)
                .orElseGet(() -> {
                    final Item item = victim.getHeldItemMainhand().getItem();
                    return EnumSet.of(item == Items.BOW ? WeaponTrait.range : WeaponTrait.melee);
                });
        this.traitsOff = Combat.getWeaponInfo(victim, EnumHand.OFF_HAND)
                .map(wo -> wo.traits)
                .orElse(EnumSet.noneOf(WeaponTrait.class));

        for (CharAttribute attr : CharAttribute.values())
            attributes.put(attr, attr.get(victim));
    }

    public boolean is(WeaponTrait trait) {
        return traitsMain.contains(trait);
    }

    public boolean isAny(WeaponTrait trait) {
        return traitsMain.contains(trait) || traitsOff.contains(trait);
    }
}
