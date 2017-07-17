package ru.ariadna.misca.crabs.characters.gui;

import net.ilexiconn.llibrary.client.gui.element.ButtonElement;
import net.ilexiconn.llibrary.client.gui.element.InputElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import ru.ariadna.misca.Misca;
import ru.ariadna.misca.crabs.characters.CharStats;
import ru.ariadna.misca.crabs.characters.Character;
import ru.ariadna.misca.crabs.characters.CharacterMessage;
import ru.ariadna.misca.crabs.characters.CharacterProvider;
import ru.ariadna.misca.gui.elements.WindowExtElement;

import java.util.Optional;
import java.util.function.Consumer;

public class CharactersWindow<T extends GuiScreen> extends WindowExtElement<T> {

    private InputElement<T> playerNameInput;
    private InputElement<T> playerStatsInput;
//    private TextInputElement<T> charsheetInput;

    private CharacterConsumer characterConsumer = new CharacterConsumer();

    public CharactersWindow(T gui) {
        super(gui, "Characters", 200, 200, (w) -> {
            Minecraft.getMinecraft().displayGuiScreen(null);
            return true;
        });

        ButtonElement requestCharBtn = new ButtonElement<>(gui, "req", 104, 16, 20, 12, b -> doRequest());
        ButtonElement updateCharBtn = new ButtonElement<>(gui, "upd", 104, 36, 20, 12, b -> doUpdate());
        playerNameInput = new InputElement<>(gui, "", 2, 16, 100, new InputElementConsumer());
        playerStatsInput = new InputElement<>(gui, "5 5 5 5 5 5", 2, 36, 100, new InputElementConsumer());
//        charsheetInput = new TextInputElement<>(gui, "", 2, 56, getWidth() - 4, getHeight() - 56 - 2, new TextInputElementConsumer());

        characterConsumer.accept(Optional.empty());

        addElement(requestCharBtn);
        addElement(updateCharBtn);
        addElement(playerNameInput);
        addElement(playerStatsInput);
//        addElement(charsheetInput);
    }

    private boolean doRequest() {
        String name = playerNameInput.getText().trim();
        if (name.length() > 1) Misca.crabs.characterProvider.request(name, characterConsumer);
        return true;
    }

    private boolean doUpdate() {
        String name = playerNameInput.getText().trim();
        if (!CharacterProvider.canEditCharacter(Minecraft.getMinecraft().thePlayer, name)) return false;

        Character c = new Character();
        c.name = name;
//        c.charsheet = charsheetInput.getLines();

        String[] stat_values = playerStatsInput.getText().split(" ");
        for (int i = 0; i < 6; i++) {
            c.stats.put(CharStats.values()[i], Byte.decode(stat_values[i]));
        }

        CharacterMessage message = new CharacterMessage();
        message.type = CharacterMessage.Type.UPDATE;
        message.character = c;
        Misca.crabs.network.sendToServer(message);

        return true;
    }

    private class InputElementConsumer implements Consumer<InputElement<T>> {
        @Override
        public void accept(InputElement inputElement) {
        }
    }

    private class CharacterConsumer implements Consumer<Optional<Character>> {
        @Override
        public void accept(Optional<Character> character) {
            if (character.isPresent()) {
                Character c = character.get();
                playerNameInput.clearText();
                playerNameInput.writeText(c.name);
//                charsheetInput.clearText();
//                charsheetInput.writeText(c.charsheet);

                playerStatsInput.clearText();
                for (CharStats cs : CharStats.values()) {
                    Byte val = c.stats.get(cs);
                    playerStatsInput.writeText((val == null ? 5 : val) + " ");
                }
            } else {
//                charsheetInput.clearText();
//                charsheetInput.setEditable(false);
            }
        }
    }
}
