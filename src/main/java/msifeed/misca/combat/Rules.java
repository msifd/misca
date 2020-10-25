package msifeed.misca.combat;

import msifeed.misca.charsheet.cap.CharAttribute;
import msifeed.misca.charsheet.cap.ICharsheet;

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
        final int strength = cs.attrs().get(CharAttribute.str);
        final int perception = cs.attrs().get(CharAttribute.per);
        final int luck = cs.attrs().get(CharAttribute.lck);

        return 0.01f + strength * 0.0025f + perception * 0.0025f + luck * 0.005f;
    }

    public static float criticalEvasion(ICharsheet cs) {
        final int endurance = cs.attrs().get(CharAttribute.end);
        final int agility = cs.attrs().get(CharAttribute.agi);
        final int luck = cs.attrs().get(CharAttribute.lck);

        return 0.01f + endurance * 0.0025f + agility * 0.0025f + luck * 0.005f;
    }
}
