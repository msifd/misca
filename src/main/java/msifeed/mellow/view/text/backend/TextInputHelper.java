package msifeed.mellow.view.text.backend;

import msifeed.mellow.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

public class TextInputHelper {
    private final TextEditorBackend backend;
    private final NavMode navMode;

    public TextInputHelper(TextEditorBackend backend, NavMode navMode) {
        this.backend = backend;
        this.navMode = navMode;
    }

    public void setCursorAtPos(int inboundX, int inboundY) {
        final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        final int tune = 1;

        final int line = backend.getView().y + inboundY / RenderUtils.lineHeight();

        final String visibleLine = backend.getLine(line).sb.substring(backend.getView().x); // Cut front part
        final int column = backend.getView().x + fr.trimStringToWidth(visibleLine, inboundX + tune).length();

        backend.setCursor(line, column);
    }

    public boolean onKeyboard(char c, int key) {
        switch (c) {
//            case 1:
//                this.setCursorPositionEnd();
//                this.setSelectionPos(0);
//                return true;
            case 3:
                GuiScreen.setClipboardString(backend.toJoinedString());
                return false;
            case 22:
                backend.insert(GuiScreen.getClipboardString());
                return true;
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
        final boolean byWord = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);

        switch (key) {
            case Keyboard.KEY_LEFT:
                backend.moveCursorColumn(byWord ? Math.min(-backend.getPrevWordLength(), -1) : -1);
                return false;
            case Keyboard.KEY_RIGHT:
                backend.moveCursorColumn(byWord ? Math.max(backend.getNextWordLength(), 1) : 1);
                return false;
            case Keyboard.KEY_UP:
                if (curLine == 0 && backend.getView().y > 0)
                    moveOffsetLine(-1);
                backend.moveCursorLine(-1);
                return false;
            case Keyboard.KEY_DOWN:
                backend.moveCursorLine(1);
                if (curLine == backend.getLinesPerView() - 1 && curLine + 1 < backend.getLineCount())
                    moveOffsetLine(1);
                return false;
            case Keyboard.KEY_BACK:
                final boolean modified = backend.remove(byWord ? Math.min(-backend.getPrevWordLength(), -1) : -1);
                if (curLine == 0 && getCursorLineInView() > 0 || backend.getView().y >= backend.getLineCount())
                    moveOffsetLine(-1);
                return modified;
            case Keyboard.KEY_DELETE:
                return backend.remove(byWord ? Math.max(backend.getNextWordLength(), 1) : 1);
            case Keyboard.KEY_RETURN:
                final boolean newline = backend.breakLine();
//                if (newline && curLine == backend.getLinesPerView() - 1 && curLine + 1 < backend.getLineCount())
//                    moveOffsetLine(1);
                return newline;
            case Keyboard.KEY_HOME:
                backend.setCursor(backend.getCursor().y, 0);
                return false;
            case Keyboard.KEY_END:
                backend.setCursor(backend.getCursor().y, backend.getCurrentLine().columns);
                return false;
            case Keyboard.KEY_PRIOR: // Page Up
                if (navMode == NavMode.PAGES)
                    backend.moveCursorLine(-(backend.getLinesPerView() + getCursorLineInView()));
                else
                    backend.moveCursorLine(-backend.getLinesPerView());
                return false;
            case Keyboard.KEY_NEXT: // Page Down
                if (navMode == NavMode.PAGES)
                    backend.moveCursorLine(backend.getLinesPerView() - getCursorLineInView());
                else
                    backend.moveCursorLine(backend.getLinesPerView());
                return false;
            default:
                return backend.insert(c);
        }
    }

    public int getCursorLineInView() {
        if (navMode == NavMode.LINES)
            return backend.getCursor().y - backend.getView().y;
        else
            return backend.getCursor().y % backend.getLinesPerView();
    }

    private void moveOffsetLine(int units) {
        if (navMode == NavMode.PAGES)
            units *= backend.getLinesPerView();
        backend.updateOffsetLine(units);
    }

    public enum NavMode {
        LINES, PAGES
    }
}
