package msifeed.misca.charsheet;

import msifeed.misca.Misca;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

public enum EffectsHandler {
    INSTANCE;

    public static class ItemEffectsConfig extends HashMap<ResourceLocation, ItemEffectInfo[]> {
    }

    public void onFoodEaten(EntityPlayer player, ItemStack stack) {
        if (player.world.isRemote) return;
        if (player.isCreative()) return;

        final HashMap<ResourceLocation, ItemEffectInfo[]> config = Misca.ITEM_EFFECTS.get();
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
