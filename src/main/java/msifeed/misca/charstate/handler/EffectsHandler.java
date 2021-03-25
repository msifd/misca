package msifeed.misca.charstate.handler;

import msifeed.misca.charstate.Charstate;
import msifeed.misca.charstate.ItemEffectInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

public class EffectsHandler {
    public void handleItemUse(EntityPlayer player, ItemStack stack) {
        final HashMap<ResourceLocation, ItemEffectInfo[]> config = Charstate.getItemEffects();
        final ItemEffectInfo[] itemEffects = config.get(stack.getItem().getRegistryName());

        if (itemEffects == null)
            return;

        for (ItemEffectInfo effectInfo : itemEffects) {
            if (effectInfo == null)
                continue;
            final Potion potion = Potion.getPotionFromResourceLocation(effectInfo.effect.toString());

            if (potion == null)
                continue;

            final PotionEffect effect = new PotionEffect(potion, effectInfo.duration * 20, effectInfo.amplifier, false, true);

            player.addPotionEffect(effect);
        }
    }
}
