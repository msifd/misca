package msifeed.misca.combat.rules;

import msifeed.misca.charsheet.CharAttribute;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.combat.Combat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.math.Vec3d;

/**
 * You know the rules and so do I
 */
public class Rules {
    public double damageIncreasePerStr = 0.04;

    public double damageIncrease(ICharsheet cs) {
        return cs.attrs().get(CharAttribute.str) * damageIncreasePerStr;
    }

    public double damageAbsorptionPerEnd = 0.04;

    public double damageAbsorption(ICharsheet cs) {
        return cs.attrs().get(CharAttribute.end) * damageAbsorptionPerEnd;
    }

    public double hitRatePerPer = 0.03;
    public double hitRatePerLck = 0.005;

    public double hitRate(ICharsheet cs, EntityLivingBase entity) {
        final double overrideRate = Combat.getConfig().getWeaponOverride(entity)
                .map(wo -> wo.hitRate).orElse(0d);

        final int perception = cs.attrs().get(CharAttribute.per);
        final int luck = cs.attrs().get(CharAttribute.lck);
        return perception * hitRatePerPer + luck * hitRatePerLck + overrideRate;
    }

    public double evasionPerAgi = 0.03;
    public double evasionPerLck = 0.005;

    public double evasion(ICharsheet cs) {
        final int agility = cs.attrs().get(CharAttribute.agi);
        final int luck = cs.attrs().get(CharAttribute.lck);
        return agility * evasionPerAgi + luck * evasionPerLck;
    }

    public double criticalHitBase = 0.01;
    public double criticalHitPerPer = 0.0025;
    public double criticalHitPerLck = 0.0075;

    public double criticalHit(ICharsheet cs) {
        final int perception = cs.attrs().get(CharAttribute.per);
        final int luck = cs.attrs().get(CharAttribute.lck);
        return criticalHitBase + perception * criticalHitPerPer + luck * criticalHitPerLck;
    }

    public double criticalEvasionBase = 0.01;
    public double criticalEvasionPerAgi = 0.0025;
    public double criticalEvasionPerLck = 0.0075;

    public double criticalEvasion(ICharsheet cs) {
        final int agility = cs.attrs().get(CharAttribute.agi);
        final int luck = cs.attrs().get(CharAttribute.lck);
        return criticalEvasionBase + agility * criticalEvasionPerAgi + luck * criticalEvasionPerLck;
    }

    public double attackApBase = 6;
    public double attackApDefault = 4;

    public double attackActionPoints(EntityLivingBase entity) {
        final double overrideAp = Combat.getConfig().getWeaponOverride(entity)
                .map(wo -> wo.ap).orElse(0d);

        final IAttributeInstance attackSpeed = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED);
        if (attackSpeed != null) {
            return attackApBase - attackSpeed.getAttributeValue() + overrideAp;
        } else {
            return attackApDefault + overrideAp;
        }
    }

    public double movementApFactor = 1;
    public double movementApRiseFactor = 1;
    public double movementApDescentFactor = 1;

    public double movementActionPoints(Vec3d from, Vec3d to) {
        final double distance = from.distanceTo(to) * movementApFactor;
        final double verticalDelta = to.y - from.y;
        if (verticalDelta > 0) {
            return distance + verticalDelta * movementApRiseFactor;
        } else if (verticalDelta < 0) {
            return distance - verticalDelta * movementApDescentFactor;
        } else {
            return distance;
        }
    }

    public double apPerMoveBase = 5;
    public double apPerMovePerAgi = 1;

    public double actionPointsPerMove(ICharsheet cs) {
        return apPerMoveBase + cs.attrs().get(CharAttribute.agi) * apPerMovePerAgi;
    }

    public double maxApBase = 0;
    public double maxApPerAgi = 4;

    public double maxActionPoints(ICharsheet cs) {
        return maxApBase + cs.attrs().get(CharAttribute.agi) * maxApPerAgi;
    }
}
