package ru.ariadna.misca.gui;

import cpw.mods.fml.client.config.GuiButtonExt;

public class GuiButtonData<T> extends GuiButtonExt {
    private T data;

    public GuiButtonData(int id, int xPos, int yPos, String displayString) {
        super(id, xPos, yPos, displayString);
    }

    public GuiButtonData(int id, int xPos, int yPos, int width, int height, String displayString) {
        super(id, xPos, yPos, width, height, displayString);
    }

    public T data() {
        return data;
    }

    public void data(T data) {
        this.data = data;
    }
}
