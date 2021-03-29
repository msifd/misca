package msifeed.misca.mixins.tconstruct;

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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.tools.ranged.BowCore;
import slimeknights.tconstruct.tools.ranged.item.CrossBow;

@Mixin(value = CrossBow.class, remap = false)
public class CrossbowMixin {
    private static final String SET_LOADED = "Lslimeknights/tconstruct/tools/ranged/item/CrossBow;setLoaded(Lnet/minecraft/item/ItemStack;Z)V";

    @Inject(method = "onPlayerStoppedUsing", at = @At(value = "INVOKE", target = SET_LOADED))
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft, CallbackInfo ci) {
        final EntityLivingBase actor = CombatFlow.getCombatActor(entity);
        if (actor == null) return;

        final WeaponInfo weapon = Combat.getWeapons().getTinckerBow(stack);
        CombatFlow.onUse(actor, weapon);
    }
}
