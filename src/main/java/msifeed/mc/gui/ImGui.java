package msifeed.mc.gui;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.mc.gui.im.ImButton;
import msifeed.mc.gui.im.ImLabel;
import msifeed.mc.gui.input.MouseTracker;
import msifeed.mc.gui.nim.NimPart;
import msifeed.mc.gui.nim.NimWindow;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;

public class ImGui {
    public static ImGui INSTANCE = new ImGui();
    public ImStyle imStyle = new ImStyle();
    public ImButton imButton = new ImButton();
    public ImLabel imLabel = new ImLabel();

    public NimWindow debugWindow = new NimWindow("Debug window");
    public NimWindow currentWindow = debugWindow;

    static {
        activate(INSTANCE);
    }

    public static void activate(ImGui imGui) {
        INSTANCE = imGui;
        MinecraftForge.EVENT_BUS.unregister(INSTANCE);
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    public void beginWindow(NimWindow window) {
        currentWindow = window;
        currentWindow.begin();
    }

    public void endWindow() {
        currentWindow.end();
        currentWindow = debugWindow;
    }

    public void verticalBlock() {
        currentWindow.pushAlignmentBlock(NimWindow.Alignment.VERTICAL);
    }

    public void horizontalBlock() {
        currentWindow.pushAlignmentBlock(NimWindow.Alignment.HORIZONTAL);
    }

    public void label(String label) {
        label(label, 0, 0);
    }

    public void label(String label, int offsetX, int offsetY) {
        final NimWindow cw = currentWindow;
        final int x = cw.nextElemX() + offsetX, y = cw.nextElemY() + offsetY;
        final int lw = imLabel.label(label, x, y, imStyle.labelColor, false);
        cw.consume(x, y, lw, imLabel.labelHeight());
    }

    public void label(String label, int width) {
        label(label, 0, 0, width, imLabel.labelHeight(), true, false);
    }

    public void label(String label, int offsetX, int offsetY, int width, int height, boolean centerWidth, boolean trim) {
        final NimWindow cw = currentWindow;
        final int x = cw.nextElemX() + offsetX, y = cw.nextElemY() + offsetY;
        imLabel.label(label, x, y, width, height, imStyle.labelColor, centerWidth, trim);
        cw.consume(x, y, width, height);
    }

    public void labelMultiline(String label, int offsetX, int offsetY) {
        final NimWindow cw = currentWindow;
        final int x = cw.nextElemX() + offsetX, y = cw.nextElemY() + offsetY;
        final int[] size = imLabel.multiline(label, x, y, imStyle.labelColor, false);
        cw.consume(x, y, size[0], size[1]);
    }

    public boolean button(String label) {
        return button(label, 0, 0);
    }

    public boolean button(String label, int width) {
        return button(label, 0, 0, width, imStyle.buttonDefaultSize.getY());
    }

    public boolean button(String label, int offsetX, int offsetY) {
        return button(label, offsetX, offsetY, imStyle.buttonDefaultSize.getX(), imStyle.buttonDefaultSize.getY());
    }

    public boolean button(String label, int offsetX, int offsetY, int width, int height) {
        final NimWindow cw = currentWindow;
        final int x = cw.nextElemX() + offsetX, y = cw.nextElemY() + offsetY;
        boolean pressed = imButton.button(label, x, y, width, height);
        currentWindow.consume(x, y, width, height);
        return pressed;
    }

    public void nim(NimPart nim) {
        final NimWindow cw = currentWindow;
        final int x = cw.nextElemX() + nim.getX(), y = cw.nextElemY() + nim.getY();
        nim.render(x, y);
        currentWindow.consume(x, y, nim.getWidth(), nim.getHeight());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onFrameBegin(RenderGameOverlayEvent.Pre event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        MouseTracker.newFrame();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onFrameEnd(RenderGameOverlayEvent.Post event) {
    }
}
