package msifeed.misca.mixins.thebetweenlands;

import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "thebetweenlands.common.herblore.elixir.effects.ElixirEffect", remap = false)
public class ElixirEffectMixin {
    @Shadow
    private ResourceLocation potionID;

    @Inject(method = "isActive", at = @At("HEAD"), cancellable = true)
    public void isActive(EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(entity.isPotionActive(getPotion()));
    }

    @Inject(method = "getStrength", at = @At("HEAD"), cancellable = true)
    public void getStrength(EntityLivingBase entity, CallbackInfoReturnable<Integer> cir) {
        if (!(entity instanceof EntityPlayer)) return;

        final Potion potion = getPotion();
        final ICharsheet sheet = CharsheetProvider.get((EntityPlayer) entity);
        if (sheet.potions().containsKey(potion))
            cir.setReturnValue(sheet.potions().get(potion));
    }

    private Potion getPotion() {
        return Potion.REGISTRY.getObject(potionID);
    }
}
