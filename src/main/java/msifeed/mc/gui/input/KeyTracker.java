package msifeed.mc.gui.input;

import org.lwjgl.input.Keyboard;

import java.util.HashSet;

public class KeyTracker {
    private static HashSet<Integer> pressedKeys = new HashSet<>();

    public static boolean isTapped(int key) {
        boolean wasPressed = pressedKeys.contains(key);
        if (Keyboard.isKeyDown(key)) {
            if (!wasPressed) {
                pressedKeys.add(key);
                return true;
            }
        } else {
            pressedKeys.remove(key);
        }
        return false;
    }
}
