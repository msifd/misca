package msifeed.mellow.view.text.backend;

import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

public class TextInputHelper {
    private final TextEditorBackend backend;
    private final NavMode navMode;

    public TextInputHelper(TextEditorBackend backend, NavMode navMode) {
        this.backend = backend;
        this.navMode = navMode;
    }

    public void onKeyboard(char c, int key) {
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
                if (curLine == 0 && backend.getView().y > 0)
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
                break;
            case Keyboard.KEY_BACK:
                backend.remove(false);
                if (curLine == 0 && getCursorLineInView() > 0 || backend.getView().y >= backend.getLineCount())
                    moveOffsetLine(-1);
                break;
            case Keyboard.KEY_RETURN:
                if (backend.breakLine() && curLine == backend.getLinesPerView() - 1 && curLine + 1 < backend.getLineCount())
                    moveOffsetLine(1);
                break;
            case Keyboard.KEY_HOME:
                backend.setCursor(backend.getCursor().y, 0);
                break;
            case Keyboard.KEY_END:
                backend.setCursor(backend.getCursor().y, backend.getCurrentLine().columns);
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
//                if (controller.insert(c) && curLine == controller.getLinesPerView() - 1 && curLine + 1 < controller.getLineCount())
//                    moveOffsetLine(1);
                break;
        }

//        lastTimePressed = System.currentTimeMillis();
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
