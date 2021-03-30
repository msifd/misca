package msifeed.misca.mixins.techguns;

import msifeed.misca.combat.Combat;
import msifeed.misca.combat.CombatFlow;
import msifeed.misca.combat.rules.WeaponInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import techguns.packets.ReloadStartedMessage;

@Mixin(value = ReloadStartedMessage.class, remap = false)
public class ReloadStartedMessageMixin {
    @Inject(method = "<init>(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/EnumHand;II)V", at = @At("RETURN"))
    public void onConstructed(EntityLivingBase shooter, EnumHand hand, int firetime, int attackType, CallbackInfo ci) {
        final EntityLivingBase actor = CombatFlow.getCombatActor(shooter);
        if (actor == null) return;

        final WeaponInfo weapon = Combat.getWeapons().get(shooter, shooter.getHeldItem(hand));
        CombatFlow.onUse(actor, weapon);
    }
}
