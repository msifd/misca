package msifeed.misca.combat;

import msifeed.misca.charsheet.cap.CharAttribute;
import msifeed.misca.charsheet.cap.ICharsheet;

public class Rules {
    public static float damageIncrease(ICharsheet cs) {
        return cs.getAttribute(CharAttribute.str) * 0.04f;
    }

    public static float damageAbsorption(ICharsheet cs) {
        return cs.getAttribute(CharAttribute.end) * 0.04f;
    }

    public static float hitRate(ICharsheet cs) {
        final int perception = cs.getAttribute(CharAttribute.per);
        final int luck = cs.getAttribute(CharAttribute.lck);

        return perception * 0.03f + luck * 0.005f;
    }

    public static float evasion(ICharsheet cs) {
        final int agility = cs.getAttribute(CharAttribute.agi);
        final int luck = cs.getAttribute(CharAttribute.lck);

        return agility * 0.03f + luck * 0.005f;
    }

    public static float criticalHit(ICharsheet cs) {
        final int perception = cs.getAttribute(CharAttribute.per);
        final int luck = cs.getAttribute(CharAttribute.lck);

        return 0.01f + perception * 0.0025f + luck * 0.0075f;
    }

    public static float criticalEvasion(ICharsheet cs) {
        final int agility = cs.getAttribute(CharAttribute.agi);
        final int luck = cs.getAttribute(CharAttribute.lck);

        return 0.01f + agility * 0.0025f + luck * 0.0075f;
    }
}
