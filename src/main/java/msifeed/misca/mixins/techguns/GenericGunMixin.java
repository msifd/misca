package msifeed.misca.mixins.techguns;

import msifeed.misca.combat.CombatFlow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "techguns.items.guns.GenericGun")
public class GenericGunMixin {
    private static final String CONSUME_AMMO_PLAYER = "Ltechguns/util/InventoryUtil;consumeAmmoPlayer(Lnet/minecraft/entity/player/EntityPlayer;[Lnet/minecraft/item/ItemStack;)Z";

    @Inject(method = "shootGunPrimary", at = @At(value = "INVOKE", target = CONSUME_AMMO_PLAYER), cancellable = true)
    public void shootGunPrimary(ItemStack stack, World world, EntityPlayer player, boolean zooming, EnumHand hand, Entity target, CallbackInfo ci) {
        final EntityLivingBase actor = CombatFlow.getCombatActor(player);
        if (actor != null && !CombatFlow.canUse(actor, stack.getItem())) {
            ci.cancel();
        }
    }

    @Inject(method = "tryForcedReload", at = @At("HEAD"), cancellable = true)
    public void tryForcedReload(ItemStack stack, World world, EntityPlayer player, EnumHand hand, CallbackInfo ci) {
        final EntityLivingBase actor = CombatFlow.getCombatActor(player);
        if (actor != null && !CombatFlow.canUse(actor, stack.getItem())) {
            ci.cancel();
        }
    }
}
