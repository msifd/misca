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

import java.util.Map;
import java.util.Set;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin {
    @Inject(method = "updatePotionEffects", at = @At("HEAD"), cancellable = true)
    public void updatePotionEffects(CallbackInfo ci) {
        final EntityLivingBase self = (EntityLivingBase) (Object) this; // Beautiful T_T

        if (!shouldUpdatePotions(self)) {
            ci.cancel();
        } else if (self instanceof EntityPlayer) {
            updateBlessPotions((EntityPlayer) self);
        }
    }

    private static boolean shouldUpdatePotions(EntityLivingBase self) {
        final ICombatant com = CombatantProvider.getOptional(self);
        if (com == null || !com.isInBattle())
            return true;

        final Battle battle = self.world.isRemote
                ? BattleStateClient.STATE
                : Combat.MANAGER.getBattle(com.getBattleId());

        return battle == null || battle.shouldUpdatePotions(self);
    }

    private static void updateBlessPotions(EntityPlayer self) {
        final ICharsheet sheet = CharsheetProvider.get(self);
        final Set<Potion> realPotions = self.getActivePotionMap().keySet();

        for (Map.Entry<Potion, Integer> e : sheet.potions().entrySet()) {
            if (realPotions.contains(e.getKey()))
                continue; // Real potions will be updated anyway
            if (e.getKey().isReady(self.ticksExisted, e.getValue())) // Use entity ticks instead of duration
                e.getKey().performEffect(self, e.getValue());
        }
    }

    @Inject(method = "isPotionActive", at = @At("RETURN"), cancellable = true)
    public void isPotionActive(Potion potion, CallbackInfoReturnable<Boolean> cir) {
        final EntityLivingBase self = (EntityLivingBase) (Object) this;
        if (!(self instanceof EntityPlayer)) return;
        if (cir.getReturnValue()) return;

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
