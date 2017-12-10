package msifeed.mc.gui.nim;

import msifeed.mc.gui.ImGui;
import msifeed.mc.gui.im.ImLabel;
import msifeed.mc.gui.ImStyle;
import msifeed.mc.gui.input.MouseTracker;
import msifeed.mc.gui.render.DrawPrimitives;
import net.minecraft.client.Minecraft;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.Point;

import java.util.function.Function;

public class NimText extends NimPart {
    private static final int MS_TO_REPEAT = 1000;

    protected ImStyle st = ImStyle.DEFAULT;
    protected String text = "";

    public Function<String, Boolean> onTextUpdate = s -> true;

    private int cursor = 0;
    private int scrollOffset = 0;
    private int frameCounter = 0; // For cursor blinking

    private int pressedKey = 0;
    private long pressedTime = 0; // For key repeating

    public NimText() {
        this(80);
    }

    public NimText(int width) {
        resize(width, 0);
    }

    @Override
    public void render() {
        frameCounter++;

        final ImLabel imLabel = ImGui.INSTANCE.imLabel;
        final Profiler profiler = Minecraft.getMinecraft().mcProfiler;
        profiler.startSection("NimText");

        // Lazy init because fonts are not loaded when the mod inits
        if (height <= 0) {
            height = imLabel.labelHeight() + st.textSpacingY * 2;
        }

        final boolean inRect = MouseTracker.isInRect(posX, posY, width, height);
        if (inRect && !MouseTracker.hovered()) {
            takeFocus();

            final Point pos = MouseTracker.pos();
            final int curX = pos.getX() - (posX + st.textSpacingX);
            final String strScrolled = text.substring(scrollOffset);
            final String strBeforeCursor = imLabel.font.trimStringToWidth(strScrolled, curX, false);
            setCursor(strBeforeCursor.length() + scrollOffset);
        }
        if (!inRect && MouseTracker.released() && inFocus()) {
            releaseFocus();
        }

        renderBackground();
        renderText();
        renderCursor();

        checkKeyEvent();

        profiler.endSection();
    }

    protected void renderBackground() {
        DrawPrimitives.drawRect(posX, posY, posX + width, posY + height, st.textBackgroundColor);
    }

    protected void renderText() {
        ImGui.INSTANCE.imLabel.label(text, posX, posY, width, height, st.textTextColor, false, true);
    }

    protected void renderCursor() {
        if (inFocus() && frameCounter / 16 % 2 == 0) {
            final ImLabel imLabel = ImGui.INSTANCE.imLabel;
            final String beforeCurText = text.substring(scrollOffset, scrollOffset + cursor);
            final int beforeCurTextLen = imLabel.font.getStringWidth(beforeCurText);
            final int curX = posX + st.textSpacingX + beforeCurTextLen;
            final int curY = posY + st.textSpacingY;
            DrawPrimitives.drawInvertedRect(curX, curY, posZ, 1, imLabel.labelHeight(), 0xFFFFFFFF);
        }
    }

    public void checkKeyEvent() {
        if (!inFocus()) return;

        // Handle key press, not release
        final boolean state = Keyboard.getEventKeyState();
        if (!state) {
            pressedKey = 0;
            pressedTime = 0;
            return;
        }

        final int key = Keyboard.getEventKey();
        final char character = Keyboard.getEventCharacter();

        // Repeating
        final long now = System.currentTimeMillis();
        if (key == pressedKey && now - pressedTime < MS_TO_REPEAT) return;
        if (key == 0) {
            pressedKey = key;
            pressedTime = now;
        }

        switch (key) {
            case Keyboard.KEY_LEFT:
                setCursor(cursor - 1);
                break;
            case Keyboard.KEY_RIGHT:
                setCursor(cursor + 1);
                break;
            case Keyboard.KEY_HOME:
                setCursor(0);
                break;
            case Keyboard.KEY_END:
                setCursor(text.length());
                break;
            case Keyboard.KEY_BACK:
                delete(-1);
                break;
            case Keyboard.KEY_DELETE:
                delete(1);
                break;
            default:
                if (ChatAllowedCharacters.isAllowedCharacter(character))
                    addCharAtCursor(character);
                break;
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (onTextUpdate.apply(text)) {
            this.text = text;
            this.cursor = text.length();
        }
    }

    public void setCursor(int pos) {
        cursor = pos;
        if (cursor < 0) cursor = 0;
        if (cursor > text.length()) cursor = text.length();
    }

    public void addCharAtCursor(char c) {
        String newText = new StringBuilder(this.text).insert(cursor, c).toString();
        if (onTextUpdate.apply(newText)) {
            this.text = newText;
            this.cursor++;
        }
    }

    public void delete(int relative) {
        int rel = cursor + relative;
        if (rel < 0 || rel > this.text.length()) return;
        String newtext;
        if (relative < 0) newtext = this.text.substring(0, rel) + this.text.substring(cursor);
        else newtext = this.text.substring(0, cursor) + this.text.substring(rel);
        if (onTextUpdate.apply(newtext)) {
            this.text = newtext;
            if (relative < 0) this.cursor = rel;
        }
    }
}
