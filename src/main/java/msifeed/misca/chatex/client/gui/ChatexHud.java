package msifeed.misca.chatex.client.gui;

import msifeed.misca.client.MiscaConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;

public class ChatexHud extends GuiNewChat {
    private final Minecraft mc = Minecraft.getMinecraft();

    public ChatexHud() {
        super(Minecraft.getMinecraft());
    }

    @Override
    public boolean getChatOpen() {
        return mc.currentScreen instanceof ChatexScreen;
    }

    @Override
    public int getChatWidth() {
        return MiscaConfig.chatSize.x;
    }

    @Override
    public int getChatHeight() {
        return MiscaConfig.chatSize.y;
    }

    @Override
    public void clearChatMessages(boolean clearSent) {
    }
}
