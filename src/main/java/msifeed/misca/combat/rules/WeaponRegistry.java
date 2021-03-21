package msifeed.misca.combat.rules;

import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

public class WeaponRegistry {
    public HashMap<ResourceLocation, WeaponInfo> overrides = new HashMap<>();
    public WeaponInfoGeneration generation = new WeaponInfoGeneration();
}
