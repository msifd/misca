package msifeed.misca.mixins.client;

import msifeed.misca.chatex.client.gui.ChatexHud;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public abstract class GuiIngameMixin {
    protected GuiNewChat persistantChatGUI;

    @Inject(at = @At("RETURN"), method = "<init>")
    public void constructor(CallbackInfo ci) {
        persistantChatGUI = new ChatexHud();
    }
}
