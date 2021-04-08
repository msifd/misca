package msifeed.misca.chatex.client.gui;

import msifeed.mellow.FocusState;
import msifeed.mellow.MellowScreen;
import msifeed.mellow.render.RenderUtils;
import msifeed.mellow.view.text.TextInput;
import msifeed.mellow.view.text.backend.AutoCompleter;
import msifeed.misca.Misca;
import msifeed.misca.chatex.ChatexRpc;
import msifeed.misca.chatex.client.TypingState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ITabCompleter;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ChatexScreen extends MellowScreen implements ITabCompleter {
    private static final int MAX_MSG_BYTES = 20000;

    private final ChatexHud hud = (ChatexHud) Minecraft.getMinecraft().ingameGUI.getChatGUI();
    private final TextInput input = new TextInput();
    private final ResizeHandle resizer = new ResizeHandle();
    private final AutoCompleter autoCompleter = new AutoCompleter(input.getBackend());

    private int historyCursor = 0;
    private String historyInputBuffer = "";

    public ChatexScreen(String text) {
        input.setSize(width, RenderUtils.lineHeight());
        input.insert(text);

        container.addView(input);
        container.addView(resizer);
    }

    @Override
    public void initGui() {
        super.initGui();

        Keyboard.enableRepeatEvents(true);

        final int inputWidth = width - 10;
        final int inputLineHeight = RenderUtils.lineHeight() + input.getRenderPref().gap;
        final int inputHeight = inputLineHeight * input.getBackend().getLineCount() + 3;
        input.setPos(5, height - inputHeight - 5, 1);
        input.setSize(inputWidth, inputHeight);
        FocusState.INSTANCE.setFocus(input);

        resizer.getScreenSize().set(width, height);
        resizer.resetPos();
    }

    @Override
    public void closeGui() {
        Keyboard.enableRepeatEvents(false);
        ConfigManager.sync(Misca.MODID, Config.Type.INSTANCE);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        final ITextComponent hovered = hud.getChatComponent(Mouse.getX(), Mouse.getY());
        if (hovered != null && hovered.getStyle().getHoverEvent() != null) {
            handleComponentHover(hovered, mouseX, mouseY);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        if (!FocusState.INSTANCE.getFocus().isPresent())
            FocusState.INSTANCE.setFocus(input);
    }

    @Override
    protected void handleWheel(int mouseX, int mouseY) {
        if (Mouse.hasWheel()) {
            hud.scroll(MathHelper.clamp(Mouse.getDWheel(), -5, 5));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            final ITextComponent tc = hud.getChatComponent(Mouse.getX(), Mouse.getY());
            if (tc != null && this.handleComponentClick(tc))
                return;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char c, int key) {
        final int linesBefore = input.getBackend().getLineCount();
        final boolean shiftPressed = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);

        switch (key) {
            case Keyboard.KEY_ESCAPE:
                Minecraft.getMinecraft().displayGuiScreen(null);
                return;
            case Keyboard.KEY_RETURN:
            case Keyboard.KEY_NUMPADENTER:
                if (shiftPressed) inputKey(c, key);
                else commitMessage();
                break;
            case Keyboard.KEY_UP:
                if (shiftPressed) setHistoryMessage(1);
                else inputKey(c, key);
                break;
            case Keyboard.KEY_DOWN:
                if (shiftPressed) setHistoryMessage(-1);
                else inputKey(c, key);
                break;
            case Keyboard.KEY_TAB:
                final boolean prev = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
                if (prev)
                    autoCompleter.completePrev();
                else
                    autoCompleter.completeNext();
                break;
            default:
                inputKey(c, key);
        }

        final int linesAfter = input.getBackend().getLineCount();
        if (linesAfter != linesBefore)
            initGui();
    }

    private void inputKey(char c, int key) {
        if (input.onKeyboard(c, key)) {
            historyCursor = 0;
            autoCompleter.reset();
            TypingState.notifyTyping();
        }
    }

    private void commitMessage() {
        final String text = input.getBackend().getLines().collect(Collectors.joining("\n  ")).trim();
        if (text.getBytes().length > MAX_MSG_BYTES) return;

        input.clear();
        if (text.isEmpty()) return;

        if (text.startsWith("/")) {
            sendChatMessage(text);
        } else {
            final EntityPlayerSP self = Minecraft.getMinecraft().player;
            ChatexRpc.sendSpeech(self.getUniqueID(), Misca.getSharedConfig().chat.getSpeechRange(0), text);
        }

        hud.addToSentMessages(text);
        historyCursor = 0;
    }

    private void setHistoryMessage(int cursorDelta) {
        final List<String> history = hud.getSentMessages();
        final int cursor = MathHelper.clamp(historyCursor + cursorDelta, 0, history.size());
        if (historyCursor == cursor) return;

        if (historyCursor == 0) {
            historyInputBuffer = input.getText().trim();
        }

        final String text = cursor == 0 ? historyInputBuffer : history.get(history.size() - cursor);

        input.clear();
        for (String line : text.split("\\R", -1)) {
            if (!input.isEmpty())
                input.insert("\n");
            input.insert(line.trim());
        }

        historyCursor = cursor;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void setCompletions(String... newCompletions) {
        autoCompleter.setCompletions(newCompletions);
        final boolean prev = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
        if (prev)
            autoCompleter.completePrev();
        else
            autoCompleter.completeNext();
    }
}
