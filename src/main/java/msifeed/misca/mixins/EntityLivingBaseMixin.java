package msifeed.misca.mixins;

import msifeed.misca.combat.Combat;
import msifeed.misca.combat.battle.Battle;
import msifeed.misca.combat.battle.BattleStateClient;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.ICombatant;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
}
