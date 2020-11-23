package msifeed.misca.combat.rules;

import msifeed.misca.charsheet.cap.CharAttribute;
import msifeed.misca.charsheet.cap.ICharsheet;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * You know the rules and so do I
 */
public class Rules {
    public static float damageIncrease(ICharsheet cs) {
        return cs.attrs().get(CharAttribute.str) * 0.04f;
    }

    public static float damageAbsorption(ICharsheet cs) {
        return cs.attrs().get(CharAttribute.end) * 0.04f;
    }

    public static float hitRate(ICharsheet cs) {
        final int perception = cs.attrs().get(CharAttribute.per);
        final int luck = cs.attrs().get(CharAttribute.lck);

        return perception * 0.03f + luck * 0.005f;
    }

    public static float evasion(ICharsheet cs) {
        final int agility = cs.attrs().get(CharAttribute.agi);
        final int luck = cs.attrs().get(CharAttribute.lck);

        return agility * 0.03f + luck * 0.005f;
    }

    public static float criticalHit(ICharsheet cs) {
        final int perception = cs.attrs().get(CharAttribute.per);
        final int luck = cs.attrs().get(CharAttribute.lck);

        return 0.01f + perception * 0.0025f + luck * 0.0075f;
    }

    public static float criticalEvasion(ICharsheet cs) {
        final int agility = cs.attrs().get(CharAttribute.agi);
        final int luck = cs.attrs().get(CharAttribute.lck);

        return 0.01f + + agility * 0.0025f + luck * 0.0075f;
    }

    public static float attackActionPoints(EntityLivingBase entity) {
        final IAttributeInstance attackSpeed = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED);
        if (attackSpeed != null) {
            return 6 - (float) attackSpeed.getAttributeValue();
        } else {
            return 4;
        }
    }

    public static float maxActionPoints(ICharsheet cs) {
        return cs.attrs().get(CharAttribute.agi) * 4;
    }
}
