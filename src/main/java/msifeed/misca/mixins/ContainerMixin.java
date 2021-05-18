package msifeed.misca.mixins;

import msifeed.misca.charstate.handler.StaminaHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Container.class)
public class ContainerMixin {
    @Inject(method = "slotChangedCraftingGrid", at = @At("HEAD"), cancellable = true)
    protected void slotChangedCraftingGrid(World world, EntityPlayer player, InventoryCrafting matrix, InventoryCraftResult result, CallbackInfo ci) {
        if (!StaminaHandler.canCraft(player, StaminaHandler.getCraftIngredients(matrix))) {
            ci.cancel();
        }
    }
}
