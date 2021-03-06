package msifeed.misca.mixins;

import msifeed.misca.charsheet.BlessingsFlow;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin {
    @Inject(method = "updatePotionEffects", at = @At("HEAD"), cancellable = true)
    public void updatePotionEffects(CallbackInfo ci) {
        final EntityLivingBase self = (EntityLivingBase) (Object) this; // Beautiful T_T

        if (self instanceof EntityPlayer) {
            BlessingsFlow.checkPotionAttributes((EntityPlayer) self);
        }

        if (!BlessingsFlow.shouldPerformPotionsEffect(self)) {
            ci.cancel();
        } else if (self instanceof EntityPlayer) {
            BlessingsFlow.performPotionEffects((EntityPlayer) self);
        }
    }

    @Inject(method = "isPotionActive", at = @At("HEAD"), cancellable = true)
    public void isPotionActive(Potion potion, CallbackInfoReturnable<Boolean> cir) {
        if (potion == MobEffects.GLOWING) cir.setReturnValue(false);

        final EntityLivingBase self = (EntityLivingBase) (Object) this;
        if (!(self instanceof EntityPlayer)) return;

        final ICharsheet sheet = CharsheetProvider.get((EntityPlayer) self);
        if (sheet.potions().containsKey(potion))
            cir.setReturnValue(true);
    }

    @Inject(method = "getActivePotionEffect", at = @At("RETURN"), cancellable = true)
    public void getActivePotionEffect(Potion potion, CallbackInfoReturnable<PotionEffect> cir) {
        final EntityLivingBase self = (EntityLivingBase) (Object) this;
        if (!(self instanceof EntityPlayer)) return;
        if (cir.getReturnValue() != null) return;

        final ICharsheet sheet = CharsheetProvider.get((EntityPlayer) self);
        final Integer amplifier = sheet.potions().get(potion);
        if (amplifier != null)
            cir.setReturnValue(new PotionEffect(potion, 32147, amplifier, false, false));
    }
}
