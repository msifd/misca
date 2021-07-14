package msifeed.misca.chatex.client;

import msifeed.misca.MiscaConfig;
import msifeed.misca.chatex.client.gui.ChatexScreen;
import msifeed.misca.client.ClientConfig;
import msifeed.misca.mixins.client.GuiChatMixin;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
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

    @SubscribeEvent
    public void onClientMsg(ClientChatReceivedEvent event) {
        if (event.getType() == ChatType.SYSTEM && MiscaConfig.client.logSystemMessages) {
            LogsSaver.logSpeech(event.getMessage());
        }
    }
}
