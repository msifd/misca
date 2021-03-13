package msifeed.misca.mixins;

import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.combat.Combat;
import msifeed.misca.combat.battle.Battle;
import msifeed.misca.combat.battle.BattleStateClient;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.ICombatant;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin {
    @Inject(at = @At("HEAD"), method = "updatePotionEffects", cancellable = true)
    public void updatePotionEffects(CallbackInfo ci) {
        final EntityLivingBase self = (EntityLivingBase) (Object) this; // Beautiful T_T

        final ICombatant com = CombatantProvider.getOptional(self);
        if (com == null || !com.isInBattle()) return;

        final Battle battle;
        if (self.world.isRemote) battle = BattleStateClient.STATE;
        else battle = Combat.MANAGER.getBattle(com.getBattleId());

        if (battle == null || !battle.shouldUpdatePotions(self))
            ci.cancel();
    }

    @Inject(at = @At("RETURN"), method = "isPotionActive", cancellable = true)
    public void isPotionActive(Potion potion, CallbackInfoReturnable<Boolean> cir) {
        final EntityLivingBase self = (EntityLivingBase) (Object) this;
        if (!(self instanceof EntityPlayer)) return;
        if (cir.getReturnValue()) return;

        final ICharsheet sheet = CharsheetProvider.get((EntityPlayer) self);
        if (sheet.potions().containsKey(potion))
            cir.setReturnValue(true);
    }

    @Inject(at = @At("RETURN"), method = "getActivePotionEffect", cancellable = true)
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
