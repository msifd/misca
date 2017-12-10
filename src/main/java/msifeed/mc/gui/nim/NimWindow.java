package msifeed.mc.gui.nim;

import msifeed.mc.gui.ImGui;
import msifeed.mc.gui.ImStyle;
import msifeed.mc.gui.input.MouseTracker;
import msifeed.mc.gui.render.DrawPrimitives;
import msifeed.mc.gui.render.TextureInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.profiler.Profiler;
import org.lwjgl.util.Point;
import org.lwjgl.util.Rectangle;

public class NimWindow {
    public String title;
    public Rectangle rect = new Rectangle(5, 5, 0, 0);
//    public Alignment alignment = Alignment.VERTICAL;

    public Runnable onCrossBtn;

    // Dragging
    private Point dragStart = null;
    private Point windowStart = null;

    public NimWindow(String title) {
        this(title, null);
    }

    public NimWindow(String title, Runnable onCrossBtn) {
        this.title = title;
        this.onCrossBtn = onCrossBtn;
    }

    public void begin() {
        final ImStyle st = ImGui.INSTANCE.imStyle;
        rect.setSize(st.windowPadding.getX(), st.windowHeaderHeight + st.windowSpacing.getY());
    }

    public void end() {
        setAlignment(Alignment.VERTICAL);
        renderWindow();
    }

    public void setAlignment(Alignment alignment) {
//        this.alignment = alignment;
    }

    public void consume(int x, int y, int width, int height) {
        final ImStyle st = ImGui.INSTANCE.imStyle;
        int nextWidth = x - rect.getX() + width + st.windowSpacing.getX();
        int nextHeight = y - rect.getY() + height + st.windowSpacing.getY();
        if (rect.getWidth() < nextWidth) rect.setWidth(nextWidth);
        if (rect.getHeight() < nextHeight) rect.setHeight(nextHeight);
    }

    public int getNextX() {
        final ImStyle st = ImGui.INSTANCE.imStyle;
        return rect.getX() + st.windowPadding.getX();
    }

    public int getNextY() {
        final ImStyle st = ImGui.INSTANCE.imStyle;
        return rect.getY() + rect.getHeight();
    }

    public void renderWindow() {
        final ImGui imgui = ImGui.INSTANCE;
        final ImStyle st = imgui.imStyle;
        final Profiler profiler = Minecraft.getMinecraft().mcProfiler;

        final int x = rect.getX(), y = rect.getY();
        final int width = rect.getWidth() + st.windowPadding.getX() - st.windowSpacing.getX(); // ???
        final int height = rect.getHeight() + st.windowPadding.getY();

        // Draw header title and buttons, calc min width
        int minWidth = 0;
        {
            final Point titleOffset = st.windowTitleOffset;
            minWidth += imgui.imLabel.label(title, x + titleOffset.getX(), y + titleOffset.getY(), width, st.windowHeaderHeight, st.windowTitleColor, false, false);
        }

        if (onCrossBtn != null) {
            final TextureInfo tex = st.windowCloseBtnTexture;
            final int cbX = x + width - tex.width + st.windowCloseBtnOffset.getX();
            final int cbY = y + st.windowCloseBtnOffset.getY();
            if (imgui.imButton.button(tex, cbX, cbY, tex.width, tex.height)) {
                onCrossBtn.run();
            }
            minWidth += st.windowSpacing.getX() + tex.width;
        }

        if (rect.getWidth() < minWidth) rect.setWidth(minWidth);

        // Handling header pressing after cross button
        final boolean inHeader = MouseTracker.isInRect(x, y, width, st.windowHeaderHeight);

        // Dragging
        if (dragStart == null) {
            if (inHeader && MouseTracker.pressed()) {
                dragStart = MouseTracker.pos();
                windowStart = new Point(rect.getX(), rect.getY());
            }
        } else {
            Point diff = MouseTracker.pos();
            diff.untranslate(dragStart);
            rect.setLocation(diff.getX() + windowStart.getX(), diff.getY() + windowStart.getY());
            if (inHeader && !MouseTracker.pressed()) {
                dragStart = null;
                windowStart = null;
            }
        }

        profiler.startSection("ImWindow");

        // Draw background
        final int midWidth = width - st.windowTopLeftTexture.width - st.windowTopRightTexture.width;
        final int midHeight = height - st.windowTopLeftTexture.height - st.windowBottomLeftTexture.height;
        final int farOffsetX = x + width - st.windowTopRightTexture.width;
        final int farOffsetY = y + height - st.windowTopLeftTexture.height;
        // Top
        DrawPrimitives.drawTexture(st.windowTopLeftTexture, x, y, -0.1);
        DrawPrimitives.drawScaledTexture(
                st.windowTopMiddleTexture,
                x + st.windowTopLeftTexture.width, y, -0.1,
                midWidth,
                st.windowTopMiddleTexture.height
        );
        DrawPrimitives.drawTexture(st.windowTopRightTexture, farOffsetX, y, -0.1);
        // Middle
        DrawPrimitives.drawScaledTexture(
                st.windowMiddleLeftTexture,
                x, y + st.windowTopLeftTexture.height, -0.1,
                st.windowMiddleLeftTexture.width,
                midHeight
        );
        DrawPrimitives.drawScaledTexture(
                st.windowMiddleMiddleTexture,
                x + st.windowMiddleLeftTexture.width, y + st.windowTopLeftTexture.height, -0.1,
                midWidth,
                midHeight
        );
        DrawPrimitives.drawScaledTexture(
                st.windowMiddleRightTexture,
                farOffsetX, y + st.windowTopLeftTexture.height, -0.1,
                st.windowMiddleRightTexture.width,
                midHeight
        );
        // Bottom
        DrawPrimitives.drawTexture(st.windowBottomLeftTexture,
                x, farOffsetY, -0.1);
        DrawPrimitives.drawScaledTexture(
                st.windowBottomMiddleTexture,
                x + st.windowTopLeftTexture.width, farOffsetY, -0.1,
                midWidth,
                st.windowBottomMiddleTexture.height
        );
        DrawPrimitives.drawTexture(st.windowBottomRightTexture,
                farOffsetX, farOffsetY, -0.1);

//        DrawPrimitives.drawRect(x, y, x + width, y + st.windowHeaderHeight, -0.1, headerColor);
//        DrawPrimitives.drawRect(x, y + st.windowHeaderHeight, x + width, y + height, -0.1, st.windowBgColor);
        profiler.endSection();
    }

    public enum Alignment {
        VERTICAL, HORIZONTAL
    }
}
