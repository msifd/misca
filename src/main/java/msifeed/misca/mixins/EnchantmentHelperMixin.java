package msifeed.misca.mixins;

import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Inject(at = @At("RETURN"), method = "getMaxEnchantmentLevel", cancellable = true)
    private static void getMaxEnchantmentLevel(Enchantment enchant, EntityLivingBase entity, CallbackInfoReturnable<Integer> cir) {
        if (!(entity instanceof EntityPlayer)) return;

        final ICharsheet sheet = CharsheetProvider.get((EntityPlayer) entity);
        final Integer value = sheet.enchants().get(enchant);
        if (value != null && value > cir.getReturnValue())
            cir.setReturnValue(value);
    }
}
