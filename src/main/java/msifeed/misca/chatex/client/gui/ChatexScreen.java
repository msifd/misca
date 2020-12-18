package msifeed.misca.chatex.client.gui;

import msifeed.mellow.MellowScreen;
import msifeed.mellow.render.RenderUtils;
import msifeed.mellow.view.text.TextInput;
import msifeed.misca.Misca;
import msifeed.misca.chatex.ChatexRpc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.stream.Collectors;

public class ChatexScreen extends MellowScreen {
    private final TextInput input = new TextInput();
    private final ChatexHud hud = (ChatexHud) Minecraft.getMinecraft().ingameGUI.getChatGUI();

    public ChatexScreen(String text) {
        input.insert(text);
        input.focus();
        container.addView(input);
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        final int inputWidth = width - 10;
        final int inputLineHeight = RenderUtils.lineHeight() + input.getRenderPref().gap;
        final int inputHeight = inputLineHeight * input.getBackend().getLineCount() + 3;
        input.setPos(5, height - inputHeight - 5, 1);
        input.setSize(inputWidth, inputHeight);
        input.getBackend().getView().setSize(inputWidth, inputHeight);
        // FIXME: fix input field view size
    }

    @Override
    public void closeGui() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        if (Mouse.hasWheel()) {
            hud.scroll(MathHelper.clamp(Mouse.getDWheel(), -5, 5));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            final ITextComponent tc = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());
            if (tc != null && this.handleComponentClick(tc)) {
                return;
            }
        }

        input.onMouseClick(mouseX, mouseY, mouseButton);
//        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char c, int key) {
        // TODO: history on up/down keys

        final int linesBefore = input.getBackend().getLineCount();

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
                // fall through to input
            default:
                super.keyTyped(c, key);
        }

        final int linesAfter = input.getBackend().getLineCount();
        if (linesAfter != linesBefore)
            initGui();
    }

    private void commitMessage() {
        final String text = input.getBackend().getLines().collect(Collectors.joining("\n  ")).trim();
        input.clear();
        if (text.isEmpty()) return;

        if (text.startsWith("/")) {
            sendChatMessage(text);
        } else {
            final EntityPlayerSP self = Minecraft.getMinecraft().player;
            ChatexRpc.sendSpeech(self.getUniqueID(), Misca.getSharedConfig().chat.getSpeechRange(0), text);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
