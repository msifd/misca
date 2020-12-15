package msifeed.misca.chatex.client.gui;

import msifeed.misca.core.GuiChatMixin;
import msifeed.misca.core.GuiIngameMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatGuiHandler {
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiChat) {
            final GuiChatMixin gui = (GuiChatMixin) event.getGui();
            event.setGui(new ChatexScreen(gui.getDefaultInputFieldText()));
        } else if (event.getGui() == null) {
            replacePersistentChatGui();
        }
    }

    public void replacePersistentChatGui() {
        final Minecraft mc = Minecraft.getMinecraft();
        if (!(mc.ingameGUI.getChatGUI() instanceof ChatexHud))
            ((GuiIngameMixin) mc.ingameGUI).setPersistantChatGUI(new ChatexHud(mc));
    }
}
