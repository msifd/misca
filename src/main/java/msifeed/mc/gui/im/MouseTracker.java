package msifeed.mc.gui.im;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class MouseTracker {
    private static boolean stillPressed = false;

    public static boolean isInRect(int x, int y, int w, int h) {
        final int[] m = ScalingHelper.scale(Mouse.getX(), Display.getHeight() - Mouse.getY());
        final int x2 = x + w
                , y2 = y + h;
        final int mx = m[0]
                , my = m[1];
        return mx >= x && mx <= x2 && my >= y && my <= y2;
    }

    public static boolean isPressed() {
        stillPressed = Mouse.isButtonDown(0);
        return stillPressed;
    }

    public static boolean isClicked() {
        final boolean next = Mouse.isButtonDown(0);
        final boolean clicked = !next && stillPressed;
        stillPressed = next;
        return clicked;
    }
}
