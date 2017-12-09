package msifeed.mc.imgui.input;

import msifeed.mc.imgui.render.ScalingHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.Point;

public class MouseTracker {
    public static ClickStatus clickStatus = ClickStatus.NONE;
    private static boolean inSomeRect = false;

    public static void newFrame() {
        if (!inSomeRect) clickStatus = ClickStatus.NONE;
        inSomeRect = false;
    }

    public static boolean isInRect(int x, int y, int w, int h) {
        final boolean inRect = inBounds(x, y, w, h);
        final boolean pressed = Mouse.isButtonDown(0);
        final boolean released = !pressed && clickStatus == ClickStatus.PRESS;
        if (inRect) {
            inSomeRect = true;
            if (pressed) clickStatus = ClickStatus.PRESS;
            else if (released) clickStatus = ClickStatus.RELEASE;
            else clickStatus = ClickStatus.HOVER;
        }
        return inRect;
    }

    protected static boolean inBounds(int x, int y, int w, int h) {
        if (Mouse.isGrabbed()) return false;
        final int[] m = ScalingHelper.scale(Mouse.getX(), Display.getHeight() - Mouse.getY());
        final int x2 = x + w, y2 = y + h;
        final int mx = m[0], my = m[1];
        return mx >= x && mx <= x2 && my >= y && my <= y2;
    }

    public static Point pos() {
//        return new Point(Mouse.getX(), Display.getHeight() - Mouse.getY());
        int[] pos = ScalingHelper.scale(Mouse.getX(), Display.getHeight() - Mouse.getY());
        return new Point(pos[0], pos[1]);
    }

    public static boolean pressed() {
        return clickStatus == MouseTracker.ClickStatus.PRESS;
    }

    public static int[] delta() {
//        return new int[]{Mouse.getDX(), -Mouse.getDY()};
        return ScalingHelper.scale(Mouse.getDX(), -Mouse.getDY());
    }

    public enum ClickStatus {
        NONE, HOVER, PRESS, RELEASE
    }
}
