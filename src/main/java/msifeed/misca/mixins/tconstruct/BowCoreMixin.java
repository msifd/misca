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

@Mixin(value = BowCore.class, remap = false)
public class BowCoreMixin {
    @Inject(method = "onItemRightClick", at = @At("HEAD"), cancellable = true)
    public void onItemRightClick(World world, EntityPlayer player, EnumHand hand, CallbackInfoReturnable<ActionResult<ItemStack>> cir) {
        final EntityLivingBase actor = CombatFlow.getCombatActor(player);
        if (actor == null) return;

        final WeaponInfo weapon = Combat.getWeapons().getTinckerBow(player.getHeldItem(hand));
        if (!CombatFlow.canAttack(actor, weapon)) {
            cir.setReturnValue(new ActionResult<>(EnumActionResult.FAIL, player.getHeldItem(hand)));
        }
    }

    private static final String FIRE_EVENT = "Lslimeknights/tconstruct/library/events/TinkerToolEvent$OnBowShoot;fireEvent(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;IF)Lslimeknights/tconstruct/library/events/TinkerToolEvent$OnBowShoot;";

    @Inject(method = "shootProjectile", at = @At(value = "INVOKE", target = FIRE_EVENT))
    public void shootProjectile(ItemStack ammo, ItemStack bow, World world, EntityPlayer player, int useTime, CallbackInfo ci) {
        if (player.world.isRemote) return;

        final EntityLivingBase actor = CombatFlow.getCombatActor(player);
        if (actor == null) return;

        final WeaponInfo weapon = Combat.getWeapons().getTinckerBow(bow);
        CombatFlow.onAttack(actor, weapon);
    }
}
