package msifeed.misca.chatex.client.gui;

import msifeed.mellow.MellowScreen;
import msifeed.mellow.view.text.TextInput;
import msifeed.misca.chatex.ChatexRpc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import org.lwjgl.input.Keyboard;

public class ChatexScreen extends MellowScreen {
    private final TextInput input = new TextInput();

    public ChatexScreen(String text) {
        input.insert(text);
        input.setPos(10, 10, 1);
        input.setSize(100, 10);
        input.focus();
        container.addView(input);
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void closeGui() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void keyTyped(char c, int key) {
        switch (key) {
            case Keyboard.KEY_ESCAPE:
                Minecraft.getMinecraft().displayGuiScreen(null);
                return;
            case Keyboard.KEY_RETURN:
            case Keyboard.KEY_NUMPADENTER:
                if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    commitMessage();
                    break;
                }
            default:
                super.keyTyped(c, key);
                break;
        }
    }

    private void commitMessage() {
        final String text = input.getText();
        input.clear();

        if (text.startsWith("/")) {
            sendChatMessage(text);
        } else {
            final EntityPlayerSP self = Minecraft.getMinecraft().player;
            ChatexRpc.sendSpeech(self.getUniqueID(), 15, text);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
