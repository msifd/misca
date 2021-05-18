package msifeed.misca.mixins.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreen.class)
public class GuiScreenMixin {
    @Inject(method = "renderToolTip", at = @At("HEAD"), cancellable = true)
    protected void renderToolTip(ItemStack stack, int x, int y, CallbackInfo ci) {
        if (stack == null) {
            ci.cancel();
        }
    }
}
