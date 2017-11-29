package msifeed.mc.gui.im;

public class ImGui {
    public static ImGui INSTANCE = new ImGui();
    public ImButton imButton = new ImButton();
    public ImLabel imLabel = new ImLabel();

    public ImGui() {
        newFrame();
    }

    public void newFrame() {
        MouseTracker.newFrame();
    }

    public void label(String label, int x, int y) {
        imLabel.label(label, x, y, 0xFFFFFFFF, false);
    }

    public void label(String label, int x, int y, int width, int height, boolean trim) {
        imLabel.label(label, x, y, width, height, 0xFFFFFFFF, false, trim);
    }

    public boolean button(String label, int x, int y) {
        return imButton.button(label, x, y, 100, 20);
    }

    public boolean button(String label, int x, int y, int width, int height) {
        return imButton.button(label, x, y, width, height);
    }
}
