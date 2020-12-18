package msifeed.mellow;

import msifeed.mellow.view.InputHandler;
import msifeed.mellow.view.View;
import msifeed.mellow.view.ViewContainer;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public abstract class MellowScreen extends GuiScreen {
    protected ViewContainer container = new ViewContainer();

    public void closeGui() {}

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        container.render();
    }

    @Override
    public final void onGuiClosed() {
        closeGui();
        FocusState.INSTANCE.clearFocus();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

//        final int xMouse = Mouse.getEventX() * this.width / this.mc.displayWidth;
//        final int yMouse = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
//
//        if (Mouse.hasWheel()) {
//            final int dWheel = Mouse.getDWheel();
//            if (dWheel != 0) {
//                container.getViewsAtPoint(xMouse, yMouse)
//                        .filter(w -> w instanceof InputHandler.MouseWheel)
//                        .findFirst()
//                        .ifPresent(w -> ((InputHandler.MouseWheel) w).onMouseWheel(xMouse, yMouse, dWheel));
//            }
//        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        final View view = container.getViewsAtPoint(mouseX, mouseY).findFirst().orElse(null);
        if (view instanceof InputHandler.MouseClick) {
            FocusState.INSTANCE.setFocus(view);
            ((InputHandler.MouseClick) view).onMouseClick(mouseX, mouseY, mouseButton);
        } else {
            FocusState.INSTANCE.clearFocus();
        }
    }

    @Override
    protected void keyTyped(char c, int key) {
        FocusState.INSTANCE.getFocus()
                .filter(view -> view instanceof InputHandler.Keyboard)
                .map(view -> (InputHandler.Keyboard) view)
                .ifPresent(view -> view.onKeyboard(c, key));
    }
}
