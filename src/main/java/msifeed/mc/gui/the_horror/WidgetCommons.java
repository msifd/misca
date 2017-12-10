package msifeed.mc.gui.the_horror;

import msifeed.mc.gui.the_horror.widget.IWidget;

public class WidgetCommons {
    private static IWidget focused = null;

    public static IWidget getFocused() {
        return focused;
    }

    public static void setFocused(IWidget focus) {
        IWidget old = focused;
        WidgetCommons.focused = focus;
        if (old != null && old != focus) old.onFocusLoose();
    }
}
