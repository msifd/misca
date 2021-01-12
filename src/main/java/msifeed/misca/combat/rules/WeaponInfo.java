package msifeed.misca.combat.rules;

import java.util.EnumSet;

public class WeaponInfo {
    public float damage = 0;
    public double apHit = 0;
    public double apUse = 0;
    public double hitRate = 0;
    public EnumSet<WeaponTrait> traits = EnumSet.noneOf(WeaponTrait.class);
}
