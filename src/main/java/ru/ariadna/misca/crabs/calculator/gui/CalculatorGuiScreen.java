package ru.ariadna.misca.crabs.calculator.gui;

import msifeed.mc.gui.WidgetGuiScreen;
import msifeed.mc.gui.layouts.HorizontalLayout;
import msifeed.mc.gui.layouts.VerticalLayout;
import msifeed.mc.gui.widgets.ButtonWidget;
import msifeed.mc.gui.widgets.LabelWidget;
import msifeed.mc.gui.widgets.TextInputWidget;

public class CalculatorGuiScreen extends WidgetGuiScreen {

    public CalculatorGuiScreen() {
        LabelWidget label = new LabelWidget("\u00A76Calculon\u00A7r - \u00A7mCRAbS\u00A7r Trial version");
        TextInputWidget input = new TextInputWidget(100, 20);
        input.setText("foo");
        ButtonWidget button = new ButtonWidget("push me", 100, 20);

        VerticalLayout ver_layout = new VerticalLayout();
        ver_layout.bindChild(label);
        ver_layout.bindChild(input);
        ver_layout.bindChild(button);
        HorizontalLayout hor_layout = new HorizontalLayout();
        hor_layout.bindChild(ver_layout);

        bindChild(hor_layout);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
