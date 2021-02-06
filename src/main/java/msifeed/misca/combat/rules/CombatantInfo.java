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

import java.util.EnumSet;

public class CombatantInfo {
    public final ICharsheet cs;
    public final Vec3d pos;
    public final int attrMod;
    private final EnumSet<WeaponTrait> traitsMain;
    private final EnumSet<WeaponTrait> traitsOff;

    public CombatantInfo(EntityLivingBase attacker, DamageSource source) {
        this.cs = CharsheetProvider.get(attacker);
        this.pos = attacker.getPositionVector();
        this.attrMod = (int) attacker.getEntityAttribute(ICharsheet.ATTRIBUTE_MOD).getAttributeValue();

        final WeaponTrait typeFromDamage = source instanceof EntityDamageSourceIndirect ? WeaponTrait.range : WeaponTrait.melee;
        this.traitsMain = Combat.getWeaponInfo(attacker, EnumHand.MAIN_HAND)
                .map(wo -> wo.traits)
                .orElse(EnumSet.of(typeFromDamage));
        this.traitsOff = Combat.getWeaponInfo(attacker, EnumHand.OFF_HAND)
                .map(wo -> wo.traits)
                .orElse(EnumSet.noneOf(WeaponTrait.class));
    }

    public CombatantInfo(EntityLivingBase victim) {
        this.cs = CharsheetProvider.get(victim);
        this.pos = CombatantProvider.get(victim).getPosition();
        this.attrMod = (int) victim.getEntityAttribute(ICharsheet.ATTRIBUTE_MOD).getAttributeValue();

        this.traitsMain = Combat.getWeaponInfo(victim, EnumHand.MAIN_HAND)
                .map(wo -> wo.traits)
                .orElseGet(() -> {
                    final Item item = victim.getHeldItemMainhand().getItem();
                    return EnumSet.of(item == Items.BOW ? WeaponTrait.range : WeaponTrait.melee);
                });
        this.traitsOff = Combat.getWeaponInfo(victim, EnumHand.OFF_HAND)
                .map(wo -> wo.traits)
                .orElse(EnumSet.noneOf(WeaponTrait.class));
    }

    public int attr(CharAttribute a) {
        return cs.attrs().get(a) + attrMod;
    }

    public boolean is(WeaponTrait trait) {
        return traitsMain.contains(trait);
    }

    public boolean isAny(WeaponTrait trait) {
        return traitsMain.contains(trait) || traitsOff.contains(trait);
    }
}
