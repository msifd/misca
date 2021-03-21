package msifeed.misca.mixins.wizardry;

import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import msifeed.misca.combat.CombatFlow;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "electroblob.wizardry.item.ItemWand")
public class ItemWandMixin {
    @Inject(method = "canCast", at = @At(value = "HEAD"), cancellable = true)
    public void canCast(ItemStack stack, Spell spell, EntityPlayer player, EnumHand hand, int castingTick, SpellModifiers modifiers, CallbackInfoReturnable<Boolean> cir) {
        final EntityLivingBase actor = CombatFlow.getCombatActor(player);
        if (actor != null && !CombatFlow.canAttack(actor, spell)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "cast", at = @At(value = "RETURN"))
    public void cast(ItemStack stack, Spell spell, EntityPlayer player, EnumHand hand, int castingTick, SpellModifiers modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            final EntityLivingBase actor = CombatFlow.getCombatActor(player);
            if (actor != null) {
                CombatFlow.onAttack(actor, spell);
            }
        }
    }
}
