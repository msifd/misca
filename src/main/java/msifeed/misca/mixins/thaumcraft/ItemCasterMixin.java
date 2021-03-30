package msifeed.misca.mixins.thaumcraft;

import msifeed.misca.combat.Combat;
import msifeed.misca.combat.CombatFlow;
import msifeed.misca.combat.rules.WeaponInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.common.items.casters.ItemCaster;

@Mixin(value = ItemCaster.class, remap = false)
public abstract class ItemCasterMixin {
    @Inject(method = "onItemRightClick", at = @At(value = "HEAD"), cancellable = true)
    public void canCast(World world, EntityPlayer player, EnumHand hand, CallbackInfoReturnable<ActionResult<ItemStack>> cir) {
        final EntityLivingBase actor = CombatFlow.getCombatActor(player);
        if (actor == null) return;

        final WeaponInfo weapon = Combat.getWeapons().get(player, player.getHeldItem(hand));
        if (!CombatFlow.canAttack(actor, weapon)) {
            cir.setReturnValue(new ActionResult<>(EnumActionResult.FAIL, player.getHeldItem(hand)));
        }
    }

    private static final String CAST_FOCUS = "Lthaumcraft/api/casters/FocusEngine;castFocusPackage(Lnet/minecraft/entity/EntityLivingBase;Lthaumcraft/api/casters/FocusPackage;)V";

    @Inject(method = "onItemRightClick", at = @At(value = "INVOKE", target = CAST_FOCUS))
    public void cast(World world, EntityPlayer player, EnumHand hand, CallbackInfoReturnable<ActionResult<ItemStack>> cir) {
        final EntityLivingBase actor = CombatFlow.getCombatActor(player);
        if (actor != null) {
            final WeaponInfo weapon = Combat.getWeapons().get(player, player.getHeldItem(hand));
            CombatFlow.onAttack(actor, weapon);
        }
    }
}
