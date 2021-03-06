package msifeed.mellow;

import msifeed.mellow.view.InputHandler;
import msifeed.mellow.view.View;
import msifeed.mellow.view.ViewContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public abstract class MellowScreen extends GuiScreen {
    protected ViewContainer container = new ViewContainer();

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        FocusState.INSTANCE.reset();

        container.setSize(width, height);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        container.render(container.getRenderGeom());
    }

    @Override
    public final void onGuiClosed() {
        closeGui();
        FocusState.INSTANCE.clearFocus();
    }

    public void closeGui() {

    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        final int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        final int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        handleMouseHover(mouseX, mouseY);
        handleWheel(mouseX, mouseY);
    }

    protected void handleMouseHover(int mouseX, int mouseY) {
        final View view = container.getViewsAtPoint(mouseX, mouseY).findFirst().orElse(null);
        if (view != null) FocusState.INSTANCE.setHover(view);
        else FocusState.INSTANCE.clearHover();
    }

    protected void handleWheel(int mouseX, int mouseY) {
        if (Mouse.hasWheel()) {
            final int dWheel = Mouse.getDWheel();
            if (dWheel != 0) {
                container.getViewsAtPoint(mouseX, mouseY)
                        .filter(w -> w instanceof InputHandler.MouseWheel)
                        .findFirst()
                        .ifPresent(w -> ((InputHandler.MouseWheel) w).onMouseWheel(mouseX, mouseY, dWheel));
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        final View view = container.getViewsAtPoint(mouseX, mouseY).findFirst().orElse(null);

        if (view instanceof InputHandler) {
            FocusState.INSTANCE.setFocus(view);
            FocusState.INSTANCE.setPress(view);
        } else {
            FocusState.INSTANCE.clearFocus();
        }

        if (view instanceof InputHandler.Mouse)
            ((InputHandler.Mouse) view).onMousePress(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        final View press = FocusState.INSTANCE.getPress().orElse(null);
        if (press == null) return;

        if (press instanceof InputHandler.Mouse)
            ((InputHandler.Mouse) press).onMouseMove(mouseX, mouseY, clickedMouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        final View press = FocusState.INSTANCE.getPress().orElse(null);
        if (press == null) return;

        if (press instanceof InputHandler.Mouse)
            ((InputHandler.Mouse) press).onMouseRelease(mouseX, mouseY, state);

        final View hover = container.getViewsAtPoint(mouseX, mouseY).findFirst().orElse(null);
        if (press == hover && press instanceof InputHandler.MouseClick)
            ((InputHandler.MouseClick) press).onMouseClick(mouseX, mouseY, state);

        FocusState.INSTANCE.clearPress();
    }

    @Override
    protected void keyTyped(char c, int key) {
        if (key == Keyboard.KEY_ESCAPE) {
            if (FocusState.INSTANCE.getFocus().isPresent())
                FocusState.INSTANCE.clearFocus();
            else
                Minecraft.getMinecraft().displayGuiScreen(null);
        }

        FocusState.INSTANCE.getFocus()
                .filter(view -> view instanceof InputHandler.Keyboard)
                .map(view -> (InputHandler.Keyboard) view)
                .ifPresent(view -> view.onKeyboard(c, key));
    }
}
