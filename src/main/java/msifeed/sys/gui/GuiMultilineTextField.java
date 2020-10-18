package msifeed.sys.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GuiMultilineTextField extends Gui {
    protected int id;
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected boolean visible = true;
    protected boolean focused = false;

    protected FontRenderer fr;
    protected final BackendMultilineText backend;

    protected NavMode navMode = NavMode.LINES;
    protected int cursorCounter;
    protected Consumer<List<String>> updateCallback;

    public GuiMultilineTextField(int id, FontRenderer fr, int x, int y, int width, int lines) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = lines * fr.FONT_HEIGHT + 4;
        this.fr = fr;

        backend = new BackendMultilineText(fr);
        backend.setMaxWidth(width - 8);
        backend.setMaxLines(lines);
        backend.setViewWidth(width - 8);
        backend.setViewHeight(height);
        backend.setLinesPerView(8);
    }

    public void setLines(List<String> lines) {
        backend.setLines(lines);
    }

    public void setCallback(Consumer<List<String>> cb) {
        updateCallback = cb;
    }

    public void updateCursorCounter() {
        cursorCounter++;
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        focused = mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height;

        if (focused && mouseButton == 0) {
            backend.setCursorAtPos(mouseX - x + 1, mouseY - y);
            return true;
        } else {
            return false;
        }
    }

    public void textboxKeyTyped(char c, int key) {
        if (!focused) return;

        switch (c) {
//            case 1:
//                this.setCursorPositionEnd();
//                this.setSelectionPos(0);
//                return true;
            case 3:
                GuiScreen.setClipboardString(backend.toJoinedString());
                return;
            case 22:
                backend.insert(GuiScreen.getClipboardString());
                onTextUpdate();
                return;
//            case 24:
//                GuiScreen.setClipboardString(this.getSelectedText());
//
//                if (this.isEnabled) {
//                    this.writeText("");
//                }
//
//                return true;
        }

        final int curLine = getCursorLineInView();

        switch (key) {
            case Keyboard.KEY_LEFT:
                backend.moveCursorColumn(false);
                break;
            case Keyboard.KEY_RIGHT:
                backend.moveCursorColumn(true);
                break;
            case Keyboard.KEY_UP:
                if (curLine == 0 && backend.getOffsetLine() > 0)
                    moveOffsetLine(-1);
                backend.moveCursorLine(-1);
                break;
            case Keyboard.KEY_DOWN:
                backend.moveCursorLine(1);
                if (curLine == backend.getLinesPerView() - 1 && curLine + 1 < backend.getLineCount())
                    moveOffsetLine(1);
                break;
            case Keyboard.KEY_DELETE:
                backend.remove(true);
                onTextUpdate();
                break;
            case Keyboard.KEY_BACK:
                backend.remove(false);
                if (curLine == 0 && getCursorLineInView() > 0 || backend.getOffsetLine() >= backend.getLineCount())
                    moveOffsetLine(-1);
                onTextUpdate();
                break;
            case Keyboard.KEY_RETURN:
                if (backend.breakLine() && curLine == backend.getLinesPerView() - 1 && curLine + 1 < backend.getLineCount())
                    moveOffsetLine(1);
                onTextUpdate();
                break;
            case Keyboard.KEY_HOME:
                backend.setCursor(backend.getCurLine(), 0);
                break;
            case Keyboard.KEY_END:
                backend.setCursor(backend.getCurLine(), backend.getCurrentLine().columns);
                break;
            case Keyboard.KEY_PRIOR: // Page Up
                if (navMode == NavMode.PAGES)
                    backend.moveCursorLine(-(backend.getLinesPerView() + getCursorLineInView()));
                else
                    backend.moveCursorLine(-backend.getLinesPerView());
                break;
            case Keyboard.KEY_NEXT: // Page Down
                if (navMode == NavMode.PAGES)
                    backend.moveCursorLine(backend.getLinesPerView() - getCursorLineInView());
                else
                    backend.moveCursorLine(backend.getLinesPerView());
                break;
            default:
                backend.insert(c);
                onTextUpdate();
//                if (backend.insert(c) && curLine == backend.getLinesPerView() - 1 && curLine + 1 < backend.getLineCount())
//                    moveOffsetLine(1);
                break;
        }
    }

    private void onTextUpdate() {
        if (updateCallback != null)
            updateCallback.accept(backend.toLineStream().collect(Collectors.toList()));
    }

    private void moveOffsetLine(int units) {
        if (navMode == NavMode.PAGES)
            units *= backend.getLinesPerView();
        backend.updateOffsetLine(units);
    }

    public void drawTextBox() {
        if (!visible) return;

        drawRect(x - 1, y - 1, x + width + 1, y + height + 1, 0xffa0a0a0);
        drawRect(x, y, x + width, y + height, 0xff000000);

        final int lineX = x + 4;
        final int[] lineY = {2};
        backend.viewLineStream().forEach(s -> {
            drawString(fr, s, lineX, y + lineY[0], 0xffffffff);
            lineY[0] += fr.FONT_HEIGHT;
        });

        if (focused && cursorCounter / 6 % 2 == 0) {
            final int curX = x + 4 + backend.getCursorXOffset();
            final int curY = y + 2 + fr.FONT_HEIGHT * getCursorLineInView();
            if (backend.getCurColumn() < backend.getCurrentLine().columns)
                Gui.drawRect(curX, curY - 1, curX + 1, curY + 1 + fr.FONT_HEIGHT, -3092272);
            else
                fr.drawString("_", curX, curY, 0x00E0E0E0);
        }
    }

    private int getCursorLineInView() {
        if (navMode == NavMode.LINES)
            return backend.getCurLine() - backend.getOffsetLine();
        else
            return backend.getCurLine() % backend.getLinesPerView();
    }

    public enum NavMode {
        LINES, PAGES
    }
}
