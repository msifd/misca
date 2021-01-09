package msifeed.misca.combat.rules;

import java.util.HashSet;
import java.util.Set;

public class WeaponInfo {
    public float damage = 0;
    public double apHit = 0;
    public double apUse = 0;
    public double hitRate = 0;
    public Set<WeaponTrait> traits = new HashSet<>();
}
