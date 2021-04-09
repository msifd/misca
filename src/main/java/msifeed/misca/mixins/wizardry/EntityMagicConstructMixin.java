package msifeed.misca.mixins.wizardry;

import electroblob.wizardry.entity.construct.EntityMagicConstruct;
import msifeed.misca.combat.Combat;
import msifeed.misca.combat.battle.Battle;
import msifeed.misca.combat.battle.BattleStateClient;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.ICombatant;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityMagicConstruct.class)
public abstract class EntityMagicConstructMixin {
    @Shadow
    public abstract EntityLivingBase getCaster();

    @Shadow
    public int lifetime;

    @Inject(method = "onUpdate", at = @At(value = "HEAD"))
    public void onUpdate(CallbackInfo ci) {
        final EntityLivingBase entity = getCaster();
        if (entity == null || lifetime == -1) return;

        final ICombatant com = CombatantProvider.getOptional(entity);
        if (com == null || !com.isInBattle()) return;

        final Battle battle = entity.world.isRemote
                ? BattleStateClient.STATE
                : Combat.MANAGER.getBattle(com.getBattleId());
        if (battle == null) return;

        if (!battle.shouldUpdatePotions(entity)) {
            // Despawn happens when ticksExisted > lifetime
            // so lets extend its lifetime when its others turn!
            lifetime += 1;
        }
    }
}
