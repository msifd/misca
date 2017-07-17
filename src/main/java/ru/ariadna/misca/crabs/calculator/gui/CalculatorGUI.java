package ru.ariadna.misca.crabs.calculator.gui;

import ru.ariadna.misca.gui.CleanElementGUI;

public class CalculatorGUI extends CleanElementGUI {
    private CalculatorWindow window;

    @Override
    public void initElements() {
        window = new CalculatorWindow<>(this);
        elementList.add(window);
    }

    @Override
    public void drawScreen(float mouseX, float mouseY, float partialTicks) {

    }

    @Override
    public void onGuiClosed() {
        window.onWindowClose();
        super.onGuiClosed();
    }
}
