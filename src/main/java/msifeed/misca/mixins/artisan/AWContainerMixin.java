package msifeed.misca.mixins.artisan;

import com.codetaylor.mc.artisanworktables.modules.worktables.gui.AWContainer;
import msifeed.misca.charstate.handler.StaminaHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AWContainer.class, remap = false)
public class AWContainerMixin {
    @Shadow
    @Final
    public int slotIndexCraftingMatrixStart;
    @Shadow
    @Final
    public int slotIndexCraftingMatrixEnd;
    @Final
    @Shadow
    private EntityPlayer player;
    @Shadow
    @Final
    private ItemStackHandler resultHandler;

    @Inject(method = "updateRecipeOutput", at = @At("HEAD"), cancellable = true)
    public void updateRecipeOutput(CallbackInfo ci) {
        final AWContainer self = (AWContainer) (Object) this;

        final int ing = StaminaHandler.getCraftIngredients(self.getInventory(), slotIndexCraftingMatrixStart, slotIndexCraftingMatrixEnd);
        if (!StaminaHandler.canCraft(player, ing)) {
            resultHandler.setStackInSlot(0, ItemStack.EMPTY);
            ci.cancel();
        }
    }
}
