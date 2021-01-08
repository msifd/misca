package msifeed.misca.combat;

import msifeed.misca.combat.rules.Rules;
import msifeed.misca.combat.rules.WeaponInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CombatConfig {
    public Rules rules = new Rules();
    public Map<ResourceLocation, WeaponInfo> weapons = new HashMap<>();

    public Optional<WeaponInfo> getWeaponInfo(EntityLivingBase entity) {
        final ResourceLocation handLoc = entity.getHeldItemMainhand().getItem().getRegistryName();
        return Optional.ofNullable(Combat.getConfig().weapons.get(handLoc));
    }
}
