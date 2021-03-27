package msifeed.misca.mixins.flan;

import com.flansmod.client.model.GunAnimations;
import com.flansmod.common.PlayerData;
import com.flansmod.common.guns.GunType;
import com.flansmod.common.guns.ItemGun;
import msifeed.misca.combat.Combat;
import msifeed.misca.combat.CombatFlow;
import msifeed.misca.combat.rules.WeaponInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemGun.class, remap = false)
public class ItemGunMixin {
    @Inject(method = "gunCantBeHandeled", at = @At(value = "HEAD"), cancellable = true)
    public void cantHandle(GunType type, EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        final EntityLivingBase actor = CombatFlow.getCombatActor(player);
        if (actor == null) return;

        final WeaponInfo weapon = Combat.getWeapons().get(player.getHeldItemMainhand());
        if (!CombatFlow.canAttack(actor, weapon)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "shoot", at = @At(value = "HEAD"))
    public void shoot(EnumHand hand, EntityPlayer player, ItemStack stack, PlayerData data, World world, GunAnimations animations, CallbackInfo ci) {
        final EntityLivingBase actor = CombatFlow.getCombatActor(player);
        if (actor == null) return;

        CombatFlow.onAttack(actor, Combat.getWeapons().get(stack));
    }

    @SideOnly(Side.CLIENT)
    @Inject(method = "CanReload", at = @At(value = "HEAD"), cancellable = true)
    public void canReload(ItemStack stack, IInventory inventory, CallbackInfoReturnable<Boolean> cir) {
        final EntityPlayer player = Minecraft.getMinecraft().player;
        final EntityLivingBase actor = CombatFlow.getCombatActor(player);
        if (actor == null) return;

        if (!CombatFlow.canUse(actor, Combat.getWeapons().get(stack))) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "Reload", at = @At(value = "HEAD"), cancellable = true)
    public void reload(ItemStack stack, World world, Entity entity, IInventory inventory, EnumHand hand, boolean hasOffHand, boolean forceReload, boolean isCreative, CallbackInfoReturnable<Boolean> cir) {
        if (!(entity instanceof EntityLivingBase)) return;

        final EntityLivingBase actor = CombatFlow.getCombatActor((EntityLivingBase) entity);
        if (actor == null) return;

        if (!CombatFlow.canUse(actor, Combat.getWeapons().get(stack))) {
            cir.setReturnValue(false);
        }
    }
}
