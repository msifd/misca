package msifeed.misca.mixins.wizardry;

import electroblob.wizardry.item.ItemScroll;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import msifeed.misca.combat.Combat;
import msifeed.misca.combat.CombatFlow;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemScroll.class, remap = false)
public class ItemScrollMixin {
    @Inject(method = "canCast", at = @At(value = "HEAD"), cancellable = true)
    public void canCast(ItemStack stack, Spell spell, EntityPlayer player, EnumHand hand, int castingTick, SpellModifiers modifiers, CallbackInfoReturnable<Boolean> cir) {
        final EntityLivingBase actor = CombatFlow.getCombatActor(player);
        if (actor == null) return;

        if (!CombatFlow.canAttack(actor, Combat.getWeapons().getSpellInfo(player, stack))) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "cast", at = @At(value = "RETURN"))
    public void cast(ItemStack stack, Spell spell, EntityPlayer player, EnumHand hand, int castingTick, SpellModifiers modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            final EntityLivingBase actor = CombatFlow.getCombatActor(player);
            if (actor == null) return;

            CombatFlow.onAttack(actor, Combat.getWeapons().getSpellInfo(player, stack));
        }
    }
}
