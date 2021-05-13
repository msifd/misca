package msifeed.misca.combat.rules;

import msifeed.misca.combat.Combat;

import java.util.Arrays;
import java.util.EnumSet;

public class WeaponInfo {
    public float dmg = 0;
    public double atk = 0;
    public double use = 0;
    public double overhead = Combat.getRules().apOverheadPart;
    public double chance = 0;
    public EnumSet<WeaponTrait> traits = EnumSet.noneOf(WeaponTrait.class);

    public transient boolean forceAtk = false;

    public WeaponInfo() {
    }

    public WeaponInfo(WeaponTrait... traits) {
        this.traits.addAll(Arrays.asList(traits));
    }

    public boolean has(WeaponTrait t) {
        return traits.contains(t);
    }
}
