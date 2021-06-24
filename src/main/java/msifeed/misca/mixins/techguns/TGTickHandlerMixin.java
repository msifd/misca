package msifeed.misca.mixins.techguns;

import msifeed.misca.combat.Combat;
import msifeed.misca.combat.CombatFlow;
import msifeed.misca.combat.rules.WeaponInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import techguns.events.TGTickHandler;

@Mixin(value = TGTickHandler.class, remap = false)
public class TGTickHandlerMixin {
    @SideOnly(Side.CLIENT)
    @Inject(method = "localClientPlayerTick", at = @At(value = "HEAD"), cancellable = true)
    private static void localClientPlayerTick(TickEvent.PlayerTickEvent event, CallbackInfo ci) {
        final EntityLivingBase actor = CombatFlow.getCombatActor(event.player);
        if (actor == null) return;

        final WeaponInfo weaponMain = Combat.getWeapons().get(event.player, event.player.getHeldItemMainhand());
        final WeaponInfo weaponOff = Combat.getWeapons().get(event.player, event.player.getHeldItemOffhand());
        if (!CombatFlow.canAttack(actor, weaponMain) && !CombatFlow.canAttack(actor, weaponOff)) {
            ci.cancel();
        }
    }
}
