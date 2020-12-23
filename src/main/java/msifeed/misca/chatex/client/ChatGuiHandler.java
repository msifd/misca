package msifeed.misca.chatex.client;

import msifeed.misca.chatex.client.gui.ChatexScreen;
import msifeed.misca.core.GuiChatMixin;
import net.minecraft.client.gui.GuiChat;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatGuiHandler {
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiChat) {
            final GuiChatMixin gui = (GuiChatMixin) event.getGui();
            event.setGui(new ChatexScreen(gui.getDefaultInputFieldText()));
        }
    }
}
