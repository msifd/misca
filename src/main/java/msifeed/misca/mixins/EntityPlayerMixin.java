package msifeed.misca.mixins;

import msifeed.misca.Misca;
import msifeed.misca.charstate.CharstateConfig;
import msifeed.misca.tweaks.HealthCareRegulations;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public class EntityPlayerMixin {
    @Inject(method = "<init>*", at = @At("RETURN"))
    public void init(CallbackInfo ci) {
        final EntityPlayer self = (EntityPlayer) (Object) this;
        HealthCareRegulations.onEntityPlayerInit(self);
    }

    @ModifyArg(method = "addExhaustion", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/FoodStats;addExhaustion(F)V"))
    public float alterExhaustion(float exhaustion) {
        final EntityPlayer self = (EntityPlayer) (Object) this;
        final CharstateConfig config = Misca.getSharedConfig().charstate;
        final float factor = 1 + config.exhaustionMod(self);
        return exhaustion * factor;
    }
}
