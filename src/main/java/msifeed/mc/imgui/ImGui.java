package msifeed.mc.imgui;

import msifeed.mc.imgui.input.MouseTracker;
import msifeed.mc.imgui.parts.ImButton;
import msifeed.mc.imgui.parts.ImLabel;
import msifeed.mc.imgui.parts.ImStyle;
import msifeed.mc.imgui.parts.ImWindow;

public class ImGui {
    public static ImGui INSTANCE = new ImGui();
    public ImStyle imStyle = new ImStyle();
    public ImButton imButton = new ImButton();
    public ImLabel imLabel = new ImLabel();

    public ImWindow debugWindow = new ImWindow("Debug window");
    public ImWindow currentWindow = debugWindow;

    public ImGui() {
        newFrame();
    }

    public void newFrame() {
        MouseTracker.newFrame();
    }

    public void beginWindow(ImWindow window) {
        currentWindow = window;
        currentWindow.begin();
    }

    public void endWindow() {
        currentWindow.end();
        currentWindow = debugWindow;
    }

    public void label(String label) {
        label(label, 0, 0);
    }

    public void label(String label, int offsetX, int offsetY) {
        final ImWindow cw = currentWindow;
        final int lw = imLabel.label(label, cw.getNextX() + offsetX, cw.getNextY() + offsetY, 0xFFFFFFFF, false);
        cw.reserve(lw, imLabel.labelHeight());
    }

//    public void label(String label, int x, int y, int width, int height) {
//        label(label, x, y, width, height, 0xFFFFFFFF, true, false);
//    }
//
//    public void label(String label, int x, int y, int width, int height, int color, boolean centerWidth, boolean trim) {
//        final ImWindow cw = currentWindow;
//        final int lw = imLabel.label(label, x, y, width, height, color, centerWidth, trim);
//        cw.reserve(lw, imLabel.labelHeight());
//    }

    public void labelMultiline(String label) {
        final ImWindow cw = currentWindow;
        final int[] size = imLabel.multiline(label, 0, 0, 0xFFFFFFFF, false);
        cw.reserve(size[0], size[1]);
    }

    public boolean button(String label) {
        return button(label, 0, 0);
    }

    public boolean button(String label, int offsetX, int offsetY) {
        return button(label, offsetX, offsetY, 80, 16);
    }

    public boolean button(String label, int offsetX, int offsetY, int width, int height) {
        final ImWindow cw = currentWindow;
        boolean pressed = imButton.button(label, cw.getNextX() + offsetX, cw.getNextY() + offsetY, width, height);
        currentWindow.reserve(width, height);
        return pressed;
    }
}
