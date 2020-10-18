package msifeed.misca.combat;

import msifeed.misca.charsheet.cap.CharAttribute;
import msifeed.misca.charsheet.cap.Charsheet;

public class Dices {
    public float hitChance(Charsheet cs) {
        // (Weapon Skill + (Agility / 5) + (Luck / 10)) * (0.75 + 0.5 * Current Fatigue / Maximum Fatigue) + Fortify Attack Magnitude + Blind Magnitude

        final int agility = cs.getAttribute(CharAttribute.agi);
        final int luck = cs.getAttribute(CharAttribute.lck);


        return (agility / 5 + luck / 10);
    }
}
