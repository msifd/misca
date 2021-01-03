package msifeed.misca.combat.rules;

import java.util.HashSet;
import java.util.Set;

public class WeaponOverride {
    public float damage = 0;
    public double ap = 0;
    public double hitRate = 0;
    public Set<WeaponTrait> traits = new HashSet<>();
}
