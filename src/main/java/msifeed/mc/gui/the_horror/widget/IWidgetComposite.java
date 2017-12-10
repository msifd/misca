package msifeed.mc.gui.the_horror.widget;

import msifeed.mc.gui.the_horror.WidgetCommons;
import msifeed.mc.gui.the_horror.event.KeyEvent;
import msifeed.mc.gui.the_horror.event.MouseEvent;

import java.util.List;

public interface IWidgetComposite extends IWidget {
    List<IWidget> getChildren();

    default void bindChild(IWidget child) {
        addChild(child);
        child.setParent(this);
    }

    default void addChild(IWidget child) {
        getChildren().add(child);
    }

    default void removeChild(IWidget child) {
        getChildren().remove(child);
    }

    default void onMouseEvent(MouseEvent event) {
        boolean found = false;
        for (IWidget w : getChildren()) {
            if (w.isPosInBounds(event.mouseX, event.mouseY)) {
                w.onMouseEvent(event);
                found = true;
            }
        }
        if (!found) WidgetCommons.setFocused(null);
    }

    default void onKeyEvent(KeyEvent event) {
        for (IWidget w : getChildren()) w.onKeyEvent(event);
    }
}
