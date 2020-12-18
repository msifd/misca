package msifeed.misca.chatex.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;

public class ChatexHud extends GuiNewChat {
    private final Minecraft mc;

    public ChatexHud(Minecraft mc) {
        super(mc);
        this.mc = mc;
    }

    @Override
    public boolean getChatOpen() {
        return mc.currentScreen instanceof ChatexScreen;
    }
}
