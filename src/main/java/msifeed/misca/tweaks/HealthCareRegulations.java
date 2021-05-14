package msifeed.misca.tweaks;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class HealthCareRegulations {
    private static final AttributeModifier BASE_INCREASE = new AttributeModifier(UUID.fromString("84dea84b-860c-4eb6-b6c5-3d380619dbb5"), "Basic life expectancy increase", 10, 0);

    public static void onEntityPlayerInit(EntityPlayer player) {
        final IAttributeInstance maxHealth = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        if (!maxHealth.hasModifier(BASE_INCREASE))
            maxHealth.applyModifier(BASE_INCREASE);
        player.setHealth(player.getMaxHealth());
    }
}
