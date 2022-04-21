package msifeed.misca.charsheet;

import com.google.gson.reflect.TypeToken;
import msifeed.misca.Misca;
import msifeed.sys.sync.SyncChannel;
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
    public static final SyncChannel<ItemEffectsConfig> ITEM_EFFECTS
            = new SyncChannel<>(Misca.RPC, "item_effects.json", TypeToken.get(ItemEffectsConfig.class));

    public static void sync() throws Exception {
        ITEM_EFFECTS.sync();
    }

    public void onFoodEaten(EntityPlayer player, ItemStack stack) {
        if (player.world.isRemote) return;
        if (player.isCreative()) return;

        final HashMap<ResourceLocation, ItemEffectInfo[]> config = ITEM_EFFECTS.get();
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
