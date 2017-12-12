package msifeed.mc.gui;

import msifeed.mc.gui.im.ImButton;
import msifeed.mc.gui.im.ImLabel;
import msifeed.mc.gui.input.MouseTracker;
import msifeed.mc.gui.nim.NimPart;
import msifeed.mc.gui.nim.NimWindow;
import org.lwjgl.util.Point;

public class ImGui {
    public static ImGui INSTANCE = new ImGui();
    public ImStyle imStyle = new ImStyle();
    public ImButton imButton = new ImButton();
    public ImLabel imLabel = new ImLabel();

    public NimWindow debugWindow = new NimWindow("Debug window");
    public NimWindow currentWindow = debugWindow;

    public ImGui() {
        newFrame();
    }

    public void newFrame() {
        MouseTracker.newFrame();
    }

    public void beginWindow(NimWindow window) {
        currentWindow = window;
        currentWindow.begin();
    }

    public void endWindow() {
        currentWindow.end();
        currentWindow = debugWindow;
    }

    public void verticalBlock() {
        currentWindow.pushAlignmentBlock(NimWindow.Alignment.VERTICAL);
    }

    public void horizontalBlock() {
        currentWindow.pushAlignmentBlock(NimWindow.Alignment.HORIZONTAL);
    }

    public void label(String label) {
        label(label, 0, 0);
    }

    public void label(String label, int offsetX, int offsetY) {
        final NimWindow cw = currentWindow;
        final int x = cw.nextElemX() + offsetX
                , y = cw.nextElemY() + offsetY;
        final int lw = imLabel.label(label, x, y, imStyle.labelColor, false);
        cw.consume(x, y, lw, 8);
    }

//    public void label(String label, int x, int y, int width, int height) {
//        label(label, x, y, width, height, 0xFFFFFFFF, true, false);
//    }
//
//    public void label(String label, int x, int y, int width, int height, int color, boolean centerWidth, boolean trim) {
//        final ImWindow cw = currentWindow;
//        final int lw = imLabel.label(label, x, y, width, height, color, centerWidth, trim);
//        cw.consume(lw, imLabel.labelHeight());
//    }

    public void labelMultiline(String label, int offsetX, int offsetY) {
        final NimWindow cw = currentWindow;
        final int x = cw.nextElemX() + offsetX
                , y = cw.nextElemY() + offsetY;
        final int[] size = imLabel.multiline(label, x, y, imStyle.labelColor, false);
        cw.consume(x, y, size[0], size[1]);
    }

    public boolean button(String label) {
        return button(label, 0, 0);
    }

    public boolean button(String label, int offsetX, int offsetY) {
        return button(label, offsetX, offsetY, 80, 12);
    }

    public boolean button(String label, int offsetX, int offsetY, int width, int height) {
        final NimWindow cw = currentWindow;
        final int x = cw.nextElemX() + offsetX
                , y = cw.nextElemY() + offsetY;
        boolean pressed = imButton.button(label, x, y, width, height);
        currentWindow.consume(x, y, width, height);
        return pressed;
    }

    public void nim(NimPart nim) {
        final NimWindow cw = currentWindow;
        final int x = cw.nextElemX() + nim.getX()
                , y = cw.nextElemY() + nim.getY();
        nim.render(x, y);
        currentWindow.consume(x, y, nim.getWidth(), nim.getHeight());
    }
}