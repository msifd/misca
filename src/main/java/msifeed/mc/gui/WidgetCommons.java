package msifeed.mc.gui;

import msifeed.mc.gui.widgets.IWidget;

public class WidgetCommons {
    private static IWidget focused = null;

    public static IWidget getFocused() {
        return focused;
    }

    public static void setFocused(IWidget focused) {
        WidgetCommons.focused = focused;
    }
}
