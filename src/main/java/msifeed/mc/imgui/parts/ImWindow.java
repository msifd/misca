package msifeed.mc.imgui.parts;

import msifeed.mc.imgui.ImGui;
import msifeed.mc.imgui.input.MouseTracker;
import msifeed.mc.imgui.render.DrawPrimitives;
import msifeed.mc.imgui.render.TextureInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.profiler.Profiler;
import org.lwjgl.util.Point;
import org.lwjgl.util.Rectangle;

public class ImWindow {
    public String title;
    public Rectangle rect = new Rectangle(5, 5, 0, 0);
    public int bodyHeight = 0;

    public Runnable onCrossBtn;

    // Dragging
    private Point dragStart = null;
    private Point windowStart = null;

    public ImWindow(String title) {
        this(title, null);
    }

    public ImWindow(String title, Runnable onCrossBtn) {
        this.title = title;
        this.onCrossBtn = onCrossBtn;
    }

    public void begin() {
        final ImStyle st = ImGui.INSTANCE.imStyle;
        rect.setSize(0, st.windowHeaderHeight + st.windowSpacingY);
        bodyHeight = 0;
    }

    public void end() {
        renderWindow();
    }

    public void reserve(int width, int height) {
        final ImStyle st = ImGui.INSTANCE.imStyle;
        final int elemWidth = width + st.windowSpacingX * 2 // Right spacing
                , elemHeight = height + st.windowSpacingY;
        bodyHeight += elemHeight;
        final int nextHeight = st.windowHeaderHeight + st.windowSpacingY + bodyHeight;
        if (rect.getWidth() < elemWidth) rect.setWidth(elemWidth);
        if (rect.getHeight() < nextHeight) rect.setHeight(nextHeight);
    }

    public int getNextX() {
        final ImStyle st = ImGui.INSTANCE.imStyle;
        return rect.getX() + st.windowSpacingX;
    }

    public int getNextY() {
        final ImStyle st = ImGui.INSTANCE.imStyle;
        return rect.getY() + st.windowHeaderHeight + st.windowSpacingY + bodyHeight;
    }

    public void renderWindow() {
        final ImGui imgui = ImGui.INSTANCE;
        final ImStyle st = imgui.imStyle;
        final Profiler profiler = Minecraft.getMinecraft().mcProfiler;

        int x = rect.getX(), y = rect.getY(), width = rect.getWidth(), height = rect.getHeight();

        // Draw header title and buttons, calc min width
        int minWidth = 0;
        minWidth += imgui.imLabel.label(title, x + st.windowSpacingX, y, width, st.windowHeaderHeight, st.windowTitleColor, false, false);

        if (onCrossBtn != null) {
            final TextureInfo tex = st.windowCrossBtnTexture;
            final int crossX = width - st.windowSpacingX - tex.width, crossY = (st.windowHeaderHeight - tex.height) / 2;
            if (imgui.imButton.button(tex, x + crossX, y + crossY, tex.width, tex.height)) {
                onCrossBtn.run();
            }
            minWidth += st.windowSpacingX + tex.width;
        }

        if (rect.getWidth() < minWidth) rect.setWidth(minWidth);

        // Handling header pressing after cross button
        final boolean inHeader = MouseTracker.isInRect(x, y, width, st.windowHeaderHeight);
        final int headerColor = inHeader
                ? (MouseTracker.pressed()
                ? st.windowHeaderColorPressed
                : st.windowHeaderColorHovered)
                : st.windowHeaderColor;

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
            if (!MouseTracker.pressed()) {
                dragStart = null;
                windowStart = null;
            }
        }

        // Draw background
        profiler.startSection("ImWindow");
        DrawPrimitives.drawRect(x, y, x + width, y + st.windowHeaderHeight, -0.1, headerColor);
        DrawPrimitives.drawRect(x, y + st.windowHeaderHeight, x + width, y + height, -0.1, st.windowBgColor);
        profiler.endSection();
    }
}
