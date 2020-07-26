package msifeed.misca.chatex.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

enum ChatGuiHandler {
    INSTANCE;

    private boolean chatIsOpened = false; // Ignore
    private char lastCharPressed = 0;

    public static void init() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && chatIsOpened)
            chatIsOpened = Minecraft.getMinecraft().currentScreen instanceof GuiChat;
    }

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!(Minecraft.getMinecraft().currentScreen instanceof GuiChat))
            return;

        final char c = Keyboard.getEventCharacter();
        if (lastCharPressed != c && Character.isAlphabetic(c)) {
            if (chatIsOpened && c != 0)
                TypingHandler.notifyTyping();
            chatIsOpened = true;
            lastCharPressed = c;
        }
    }
}
