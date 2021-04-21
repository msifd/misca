package msifeed.misca.mixins;

import msifeed.misca.charstate.Charstate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFood.class)
public class ItemFoodMixin {
    @Inject(method = "onFoodEaten", at = @At("HEAD"))
    public void addStats(ItemStack stack, World world, EntityPlayer player, CallbackInfo ci) {
        Charstate.INSTANCE.onFoodEaten(player, stack);
    }
}
