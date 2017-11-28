package msifeed.mc.gui.im;

public class ImGui {
    public static ImGui INSTANCE = new ImGui();
    public ImButton imButton = new ImButton();


    public ImGui() {
        newFrame();
    }

    public void newFrame() {
        // TODO Poll mouse release in inter-widget space
    }

    public boolean button(String label, int x, int y) {
        return imButton.button(label, x, y, 100, 20);
    }

    public boolean button(String label, int x, int y, int width, int height) {
        return imButton.button(label, x, y, width, height);
    }
}
