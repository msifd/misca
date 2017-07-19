package msifeed.mc.gui;

import msifeed.mc.gui.widgets.IWidget;

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
