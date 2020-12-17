package msifeed.mellow;

import msifeed.mellow.view.IKeysHandler;
import msifeed.mellow.view.ViewContainer;
import net.minecraft.client.gui.GuiScreen;

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
    protected void keyTyped(char c, int key) {
        FocusState.INSTANCE.getFocus()
                .filter(view -> view instanceof IKeysHandler)
                .ifPresent(view -> ((IKeysHandler) view).onKeyboard(c, key));
    }
}
