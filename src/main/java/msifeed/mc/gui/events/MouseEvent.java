package msifeed.mc.gui.events;

public class MouseEvent {
    public Type type;
    public int mouseX, mouseY;
    public int button;

    public MouseEvent(int mouseX, int mouseY, int button, Type type) {
        this.type = type;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.button = button;
    }

    public enum Type {
        PRESS, MOVE, RELEASE
    }
}
