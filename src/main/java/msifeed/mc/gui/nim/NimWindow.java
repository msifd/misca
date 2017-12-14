package msifeed.mc.gui.nim;

import msifeed.mc.gui.ImGui;
import msifeed.mc.gui.ImStyle;
import msifeed.mc.gui.input.MouseTracker;
import msifeed.mc.gui.render.DrawPrimitives;
import msifeed.mc.gui.render.TextureInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.profiler.Profiler;
import org.lwjgl.util.Point;

public class NimWindow {
    public final Point pos = new Point(5, 5);
    public final Point size = new Point(0, 0);
    protected final Point dragStart = new Point();
    protected final Point windowStart = new Point();
    protected final Point prevBlock = new Point();
    protected final Point currBlock = new Point();
    public String title;
    public Runnable onCrossBtn = () -> {
    };
    // Dragging
    protected boolean dragging = false;
    // Alignment
    protected Alignment alignment = Alignment.VERTICAL;

    public NimWindow(String title) {
        this(title, null);
    }

    public NimWindow(String title, Runnable onCrossBtn) {
        this.title = title;
        this.onCrossBtn = onCrossBtn;
    }

    public void begin() {
        final ImStyle st = ImGui.INSTANCE.imStyle;
        size.setLocation(0, 0);
        alignment = Alignment.VERTICAL;
        currBlock.setLocation(0, st.windowHeaderHeight);
        pushAlignmentBlock(Alignment.VERTICAL);
    }

    public void end() {
        final ImStyle st = ImGui.INSTANCE.imStyle;
        pushAlignmentBlock(Alignment.VERTICAL);
//        size.translate(st.windowSpacing);
        renderWindow();
    }

    public void pushAlignmentBlock(Alignment alignment) {
        // Window eats block
        final ImStyle st = ImGui.INSTANCE.imStyle;
        if (size.getX() < currBlock.getX()) size.setX(currBlock.getX());
        if (size.getY() < currBlock.getY()) size.setY(currBlock.getY());
        prevBlock.setLocation(currBlock);
        currBlock.setLocation(st.windowPadding.getX(), prevBlock.getY());
        this.alignment = alignment;
    }

    public void consume(int x, int y, int width, int height) {
        // Block eats element
        final ImStyle st = ImGui.INSTANCE.imStyle;
        int widthWithOffset = x - pos.getX() + width + st.windowSpacing.getX();
        int heightWithOffset = y - pos.getY() + height + st.windowSpacing.getY();
        if (currBlock.getX() < widthWithOffset) currBlock.setX(widthWithOffset);
        if (currBlock.getY() < heightWithOffset) currBlock.setY(heightWithOffset);
    }

    public int nextElemX() {
        final ImStyle st = ImGui.INSTANCE.imStyle;
        return pos.getX()
                + (alignment == Alignment.HORIZONTAL
                ? currBlock.getX()
                : st.windowPadding.getX());
    }

    public int nextElemY() {
        return pos.getY()
                + (alignment == Alignment.HORIZONTAL
                ? prevBlock.getY()
                : currBlock.getY());
    }

    public int getBlockContentWidth() {
        final ImStyle st = ImGui.INSTANCE.imStyle;
        return currBlock.getX() - st.windowSpacing.getX() - st.windowPadding.getX();
    }

    public void renderWindow() {
        final ImGui imgui = ImGui.INSTANCE;
        final ImStyle st = imgui.imStyle;
        final Profiler profiler = Minecraft.getMinecraft().mcProfiler;

        final int x = pos.getX(), y = pos.getY();
        // Spacing added after the last element, remove it
        final int width = size.getX() + st.windowPadding.getX() - st.windowSpacing.getX();
        final int height = size.getY() + st.windowPadding.getY() - st.windowSpacing.getY();

        // Draw header title and buttons, calc min width
        {
            int minWidth = 0;
            final Point titleOffset = st.windowTitleOffset;
            minWidth += imgui.imLabel.label(title, x + titleOffset.getX(), y + titleOffset.getY(), width, st.windowHeaderHeight, st.windowTitleColor, false, false);

            final TextureInfo tex = st.windowCloseBtnTexture;
            final int cbX = x + width - tex.width + st.windowCloseBtnOffset.getX();
            final int cbY = y + st.windowCloseBtnOffset.getY();
            if (imgui.imButton.button(tex, cbX, cbY, tex.width, tex.height)) {
                onCrossBtn.run();
            }
            minWidth += st.windowSpacing.getX() + tex.width;

            if (currBlock.getX() < minWidth) currBlock.setX(minWidth);
        }

        // Handling header pressing after cross button
        final boolean inHeader = MouseTracker.isInRect(x, y,
                width - st.windowCloseBtnTexture.width + st.windowCloseBtnOffset.getX(), st.windowHeaderHeight);

        // Dragging
        if (!dragging) {
            if (inHeader && MouseTracker.pressed()) {
                dragging = true;
                dragStart.setLocation(MouseTracker.pos());
                windowStart.setLocation(pos.getX(), pos.getY());
            }
        } else {
            Point diff = MouseTracker.pos();
            diff.untranslate(dragStart);
            pos.setLocation(diff.getX() + windowStart.getX(), diff.getY() + windowStart.getY());
            if (inHeader && !MouseTracker.pressed()) {
                dragging = false;
            }
        }

        profiler.startSection("ImWindow");

        // Draw background
        final int midWidth = width - st.windowTopLeftTexture.width - st.windowTopRightTexture.width;
        final int midHeight = height - st.windowTopLeftTexture.height - st.windowBottomLeftTexture.height;
        final int farOffsetX = x + width - st.windowBottomRightTexture.width;
        final int farOffsetY = y + height - st.windowBottomLeftTexture.height;
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

//    protected void expand(Point expandable, int width, int height) {
//        if (expandable.getX() < width) expandable.setX(width);
//        if (expandable.getY() < height) expandable.setY(height);
//    }

    public enum Alignment {
        VERTICAL, HORIZONTAL
    }
}
