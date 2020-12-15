package msifeed.misca.core;

import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiIngame.class)
public interface GuiIngameMixin {
    @Accessor
    void setPersistantChatGUI(GuiNewChat chat);
}