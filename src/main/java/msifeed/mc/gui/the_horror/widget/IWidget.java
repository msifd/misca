package msifeed.mc.gui.the_horror.widget;

import msifeed.mc.gui.the_horror.WidgetCommons;
import msifeed.mc.gui.the_horror.event.KeyEvent;
import msifeed.mc.gui.the_horror.event.MouseEvent;
import net.minecraft.client.Minecraft;

public interface IWidget {
    int getPosX();

    void setPosX(int posX);

    int getPosY();

    void setPosY(int posY);

    int getWidth();

    int getHeight();

    IWidgetComposite getParent();

    void setParent(IWidgetComposite parent);

    void render(Minecraft mc, int mouseX, int mouseY, float tick);

    default int getPossibleWidth() {
        return getWidth();
    }

    default int getPossibleHeight() {
        return getHeight();
    }

    default int getAbsPosX() {
        final IWidgetComposite parent = getParent();
        return parent != null ? parent.getAbsPosX() + getPosX() : getPosX();
    }

    default int getAbsPosY() {
        final IWidgetComposite parent = getParent();
        return parent != null ? parent.getAbsPosY() + getPosY() : getPosY();
    }

    default void update() {
    }

    default void onMouseEvent(MouseEvent event) {
        if (event.type == MouseEvent.Type.PRESS) WidgetCommons.setFocused(null);
    }

    default void onKeyEvent(KeyEvent event) {
    }

    default boolean isPosInBounds(int x, int y) {
        int posX = getAbsPosX(), posY = getAbsPosY();
        return x >= posX && x < posX + getPossibleWidth() && y >= posY && y < posY + getPossibleHeight();
    }

    default boolean isFocused() {
        return WidgetCommons.getFocused() == this;
    }

    default void setFocused() {
        WidgetCommons.setFocused(this);
    }

    default void onFocusLoose() {
    }
}
