package ru.ariadna.misca.crabs.characters.gui;

import net.ilexiconn.llibrary.client.gui.element.ButtonElement;
import net.ilexiconn.llibrary.client.gui.element.InputElement;
import net.ilexiconn.llibrary.client.gui.element.WindowElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import ru.ariadna.misca.crabs.Crabs;
import ru.ariadna.misca.crabs.characters.Character;
import ru.ariadna.misca.crabs.characters.CharacterMessage;
import ru.ariadna.misca.crabs.characters.CharacterProvider;
import ru.ariadna.misca.gui.WindowExtElement;

import java.util.Optional;
import java.util.function.Consumer;

public class CharactersWindow<T extends GuiScreen> extends WindowExtElement<T> {

    private InputElement playerNameInput;
    private InputElement playerStatsInput;
    private InputElement charsheetInput;

    public CharactersWindow(T gui) {
        super(gui, "Characters", 200, 200, (w) -> {
            Minecraft.getMinecraft().displayGuiScreen(null);
            return true;
        });

        ButtonElement requestCharBtn = new ButtonElement<>(gui, "req", 104, 16, 20, 12, b -> doRequest());
        ButtonElement updateCharBtn = new ButtonElement<>(gui, "upd", 104, 36, 20, 12, b -> doUpdate());
        playerNameInput = new InputElement(gui, "", 2, 16, 100, new InputElementConsumer());
        playerStatsInput = new InputElement(gui, "", 2, 36, 100, new InputElementConsumer());
        charsheetInput = new InputElement(gui, "", 2, 56, getWidth() - 4, new InputElementConsumer());
        charsheetInput.setHeight(getHeight() - 56 - 2);
        charsheetInput.setEditable(false);

        addElement(requestCharBtn);
        addElement(updateCharBtn);
        addElement(playerNameInput);
        addElement(playerStatsInput);
        addElement(charsheetInput);
    }

    private boolean doRequest() {
        String name = playerNameInput.getText().trim();
        if (name.length() > 1) Crabs.instance.characterProvider.request(name, new CharacterConsumer());
        return true;
    }

    private boolean doUpdate() {
        String name = playerNameInput.getText().trim();
        if (!CharacterProvider.canEditCharacter(Minecraft.getMinecraft().thePlayer, name)) return false;

        Character c = new Character();
        c.name = name;
        c.charsheet = charsheetInput.getText();

        CharacterMessage message = new CharacterMessage();
        message.type = CharacterMessage.Type.UPDATE;
        message.character = c;
        Crabs.instance.network.sendToServer(message);

        return true;
    }

    private class InputElementConsumer implements Consumer<InputElement> {
        @Override
        public void accept(InputElement inputElement) {
        }
    }

    private class CharacterConsumer implements Consumer<Optional<Character>> {
        @Override
        public void accept(Optional<Character> character) {
            charsheetInput.clearText();
            if (character.isPresent()) {
                charsheetInput.writeText(character.get().charsheet);
            } else {
                charsheetInput.writeText("Character not found.");
            }
        }
    }
}
