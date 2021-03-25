package msifeed.misca.charstate.handler;

import msifeed.misca.charstate.Charstate;
import msifeed.misca.charstate.ItemEffectsConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class EffectsHandler {
    public void handleItemUse(EntityPlayer player, ItemStack stack) {
        final ItemEffectsConfig config = Charstate.getItemEffectsConfig();

        try {
            final String usedItemName = stack.getItem().getRegistryName().toString();
            for (String line : config.items) {
                String[] effectData = line.split(",");
                if (effectData.length == 4) {
                    final String itemName = effectData[0];

                    if (itemName.equalsIgnoreCase(usedItemName)) {
                        final String effectName = effectData[1];
                        final int effectPower = Integer.parseInt(effectData[2]);
                        final int effectDuration = Integer.parseInt(effectData[3]) * 20;

                        final ResourceLocation potionId = new ResourceLocation(effectName);
                        final Potion potion = Potion.REGISTRY.getObject(potionId);

                        if (potion != null) {
                            final PotionEffect effect = new PotionEffect(potion, effectDuration, effectPower, true, false);

                            player.addPotionEffect(effect);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
