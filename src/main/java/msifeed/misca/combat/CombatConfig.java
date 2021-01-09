package msifeed.misca.combat;

import msifeed.misca.combat.rules.Rules;
import msifeed.misca.combat.rules.WeaponInfo;
import msifeed.misca.combat.rules.WeaponTrait;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CombatConfig {
    public Rules rules = new Rules();
    public Map<ResourceLocation, WeaponInfo> weapons = new HashMap<>();

    public CombatConfig() {
        final WeaponInfo shield = new WeaponInfo();
        shield.traits.add(WeaponTrait.ignoreUsage);
        weapons.put(Items.SHIELD.getRegistryName(), shield);
    }

    public Optional<WeaponInfo> getWeaponInfo(Item item) {
        final ResourceLocation handLoc = item.getRegistryName();
        return Optional.ofNullable(Combat.getConfig().weapons.get(handLoc));
    }

    public Optional<WeaponInfo> getWeaponInfo(EntityLivingBase entity, EnumHand hand) {
        final ResourceLocation handLoc = entity.getHeldItem(hand).getItem().getRegistryName();
        return Optional.ofNullable(Combat.getConfig().weapons.get(handLoc));
    }
}
