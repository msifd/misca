package ru.ariadna.misca.gui;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.ElementGUI;
import net.ilexiconn.llibrary.client.gui.element.color.ColorMode;

public abstract class CleanElementGUI extends ElementGUI {
    private static final ColorMode colorMode = ColorMode.create("light-nobg", 0xFFCDCDCD, 0xFFACACAC, 0, 0xFFCDCDCD, 0xFFC2C2C2, 0xFF000000, 0xFFFFFFFF);

    @Override
    public void initGui() {
        LLibrary.CONFIG.setColorMode("light-nobg");
        super.initGui();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
