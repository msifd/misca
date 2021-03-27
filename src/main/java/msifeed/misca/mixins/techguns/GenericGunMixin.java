package msifeed.misca.mixins.techguns;

import msifeed.misca.combat.Combat;
import msifeed.misca.combat.CombatFlow;
import msifeed.misca.combat.rules.WeaponInfo;
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
import techguns.items.guns.GenericGun;

@Mixin(value = GenericGun.class, remap = false)
public class GenericGunMixin {
    private static final String CONSUME_AMMO_PLAYER = "Ltechguns/util/InventoryUtil;consumeAmmoPlayer(Lnet/minecraft/entity/player/EntityPlayer;[Lnet/minecraft/item/ItemStack;)Z";

    @Inject(method = "shootGunPrimary", at = @At(value = "HEAD"), cancellable = true)
    public void shoot(ItemStack stack, World world, EntityPlayer player, boolean zooming, EnumHand hand, Entity target, CallbackInfo ci) {
        final EntityLivingBase actor = CombatFlow.getCombatActor(player);
        if (actor == null) return;

        final WeaponInfo weapon = Combat.getWeapons().get(stack);
        if (CombatFlow.canAttack(actor, weapon)) {
            CombatFlow.onAttack(actor, weapon);
        } else {
            ci.cancel();
        }
    }

    @Inject(method = "shootGunPrimary", at = @At(value = "INVOKE", target = CONSUME_AMMO_PLAYER), cancellable = true)
    public void shootReload(ItemStack stack, World world, EntityPlayer player, boolean zooming, EnumHand hand, Entity target, CallbackInfo ci) {
        final EntityLivingBase actor = CombatFlow.getCombatActor(player);
        if (actor == null) return;

        if (!CombatFlow.canUse(actor, Combat.getWeapons().get(stack))) {
            ci.cancel();
        }
    }

    @Inject(method = "tryForcedReload", at = @At("HEAD"), cancellable = true)
    public void forcedReload(ItemStack stack, World world, EntityPlayer player, EnumHand hand, CallbackInfo ci) {
        final EntityLivingBase actor = CombatFlow.getCombatActor(player);
        if (actor == null) return;

        if (!CombatFlow.canUse(actor, Combat.getWeapons().get(stack))) {
            ci.cancel();
        }
    }
}
