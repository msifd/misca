package ru.ariadna.misca.crabs.characters.gui;

import ru.ariadna.misca.gui.CleanElementGUI;

public class CharactersGUI extends CleanElementGUI {
    public static final CharactersGUI instance = new CharactersGUI();

    @Override
    public void initElements() {
        elementList.add(new CharactersWindow<>(this));
    }

    @Override
    public void drawScreen(float mouseX, float mouseY, float partialTicks) {

    }
}
