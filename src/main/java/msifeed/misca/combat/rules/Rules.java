package msifeed.misca.combat.rules;

import msifeed.misca.charsheet.CharAttribute;
import msifeed.misca.charsheet.ICharsheet;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.math.Vec3d;

/**
 * You know the rules and so do I
 */
public class Rules {
    public static double damageIncrease(ICharsheet cs) {
        return cs.attrs().get(CharAttribute.str) * 0.04;
    }

    public static double damageAbsorption(ICharsheet cs) {
        return cs.attrs().get(CharAttribute.end) * 0.04;
    }

    public static double hitRate(ICharsheet cs) {
        final int perception = cs.attrs().get(CharAttribute.per);
        final int luck = cs.attrs().get(CharAttribute.lck);

        return perception * 0.03 + luck * 0.005;
    }

    public static double evasion(ICharsheet cs) {
        final int agility = cs.attrs().get(CharAttribute.agi);
        final int luck = cs.attrs().get(CharAttribute.lck);

        return agility * 0.03 + luck * 0.005;
    }

    public static double criticalHit(ICharsheet cs) {
        final int perception = cs.attrs().get(CharAttribute.per);
        final int luck = cs.attrs().get(CharAttribute.lck);

        return 0.01 + perception * 0.0025 + luck * 0.0075;
    }

    public static double criticalEvasion(ICharsheet cs) {
        final int agility = cs.attrs().get(CharAttribute.agi);
        final int luck = cs.attrs().get(CharAttribute.lck);

        return 0.01 + + agility * 0.0025 + luck * 0.0075;
    }

    public static double attackActionPoints(EntityLivingBase entity) {
        final IAttributeInstance attackSpeed = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED);
        if (attackSpeed != null) {
            return 6 - attackSpeed.getAttributeValue();
        } else {
            return 4;
        }
    }

    public static double movementActionPoints(Vec3d from, Vec3d to) {
        final double distance = from.distanceTo(to);
        final double verticalDelta = to.y - from.y;
        if (verticalDelta > 0) {
            return distance + verticalDelta;
        } else if (verticalDelta < 0) {
            return distance + verticalDelta;
        } else {
            return distance;
        }
    }

    public static double actionPointsPerMove(ICharsheet cs) {
        return 5 + cs.attrs().get(CharAttribute.agi);
    }

    public static float maxActionPoints(ICharsheet cs) {
        return cs.attrs().get(CharAttribute.agi) * 4;
    }
}
