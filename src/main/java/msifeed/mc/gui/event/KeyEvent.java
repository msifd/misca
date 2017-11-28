package msifeed.mc.gui.event;

public class KeyEvent {
    public char character;
    public int key;

    public KeyEvent(char character, int key) {
        this.character = character;
        this.key = key;
    }
}
