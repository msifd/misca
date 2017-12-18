package msifeed.mc.gui.nim;

import msifeed.mc.gui.ImStyle;
import msifeed.mc.gui.NimGui;
import msifeed.mc.gui.im.ImLabel;
import msifeed.mc.gui.input.KeyTracker;
import msifeed.mc.gui.input.MouseTracker;
import msifeed.mc.gui.render.DrawPrimitives;
import msifeed.mc.gui.render.DrawTexbox;
import net.minecraft.client.Minecraft;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.Point;

import java.util.function.Function;

public class NimText extends NimPart {
    private static final int MS_TO_REPEAT = 500;
    public Function<String, Boolean> validateText = s -> true;
    public boolean centerByWidth = false;
    protected ImStyle st = ImStyle.DEFAULT;
    protected String text = "";
    private int cursor = 0;
    private int scrollOffset = 0;
    private int frameCounter = 0; // For cursor blinking

    private int pressedKey = -1;
    private long pressedTime = 0; // For key repeating

    public NimText() {
        this(70);
    }

    public NimText(int width) {
        resize(width, NimGui.INSTANCE.imStyle.textDefaultHeight);
    }

    public NimText(int width, int height) {
        resize(width, height);
    }

    @Override
    public void render(int x, int y) {
        frameCounter++;

        final ImLabel imLabel = NimGui.INSTANCE.imLabel;
        final Profiler profiler = Minecraft.getMinecraft().mcProfiler;
        profiler.startSection("NimText");

        final boolean inRect = MouseTracker.isInRect(x, y, width, height);
        if (inRect && !MouseTracker.hovered()) {
            takeFocus();

            final Point pos = MouseTracker.pos();
            final int curX = pos.getX() - (x + st.textLabelOffsetX);
            final String strScrolled = text.substring(scrollOffset);
            final String strBeforeCursor = imLabel.font.trimStringToWidth(strScrolled, curX, false);
            setCursor(strBeforeCursor.length() + scrollOffset);
        }
        if (inFocus() && (KeyTracker.isPressed(Keyboard.KEY_ESCAPE) || !inRect && MouseTracker.pressed())) {
            releaseFocus();
        }

        // Render background
        {
            final int vOffset = (inRect ? 1 : 0) * st.textLeftTexture.height;
            DrawTexbox.threeParted(
                    st.textLeftTexture, st.textMiddleTexture, st.textRightTexture,
                    x, y, width, height, vOffset);
        }

        final int labelX = x + (centerByWidth
                ? (width - NimGui.INSTANCE.imLabel.font.getStringWidth(text)) / 2
                : st.textLabelOffsetX);

        // Render text
        {
            NimGui.INSTANCE.imLabel.label(text, labelX, y,
                    width, height, st.textLabelColor, false, true);
        }

        // Render cursor
        if (inFocus() && frameCounter / 16 % 2 == 0) {
            final String beforeCurText = text.substring(scrollOffset, scrollOffset + cursor);
            final int beforeCurTextLen = imLabel.font.getStringWidth(beforeCurText);
            final int curX = labelX + beforeCurTextLen;
            final int curY = y + 1;
            DrawPrimitives.drawInvertedRect(curX, curY, posZ, 1, st.textCursorHeight, 0xFFFFFFFF);
        }

        checkKeyEvent();

        profiler.endSection();
    }

    public void checkKeyEvent() {
        if (!inFocus()) return;

        // Handle key press, not release
        final boolean state = Keyboard.getEventKeyState();
        if (!state) {
            pressedKey = -1;
            pressedTime = 0;
            return;
        }

        final int key = Keyboard.getEventKey();
        final char character = Keyboard.getEventCharacter();

        // Repeating
        final long now = System.currentTimeMillis();
        if (now - pressedTime < MS_TO_REPEAT) return;
        if (pressedKey != key) {
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
        if (validateText.apply(text)) {
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
        if (validateText.apply(newText)) {
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
        if (validateText.apply(newtext)) {
            this.text = newtext;
            if (relative < 0) this.cursor = rel;
        }
    }
}
