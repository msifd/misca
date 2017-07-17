package ru.ariadna.misca.crabs.calculator.gui;

import net.ilexiconn.llibrary.client.gui.element.ButtonElement;
import net.ilexiconn.llibrary.client.gui.element.InputElement;
import net.ilexiconn.llibrary.client.gui.element.LabelElement;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import ru.ariadna.misca.Misca;
import ru.ariadna.misca.config.ConfigManager;
import ru.ariadna.misca.crabs.calculator.MoveMessage;
import ru.ariadna.misca.crabs.characters.CharStats;
import ru.ariadna.misca.crabs.characters.Character;
import ru.ariadna.misca.crabs.combat.parts.ActionType;
import ru.ariadna.misca.gui.elements.WindowExtElement;

import java.io.*;
import java.util.function.Consumer;

class CalculatorWindow<T extends GuiScreen> extends WindowExtElement<T> {
    private static Character character = loadChar();
    private InputElement[] stat_inputs = new InputElement[6];
    private InputElement<T> mod_input;
    private TwoDigitConsumer consumer = new TwoDigitConsumer();

    CalculatorWindow(T gui) {
        super(gui, "\u00A76Calculon\u00A7r - CRAbS Trial version", 180, 106);

        stat_inputs[0] = new InputElement<>(gui, String.valueOf(character.get(CharStats.STRENGTH)), 22, 16, 20, true, consumer, CalculatorWindow::digit);
        stat_inputs[1] = new InputElement<>(gui, String.valueOf(character.get(CharStats.REFLEXES)), 64, 16, 20, true, consumer, CalculatorWindow::digit);
        stat_inputs[2] = new InputElement<>(gui, String.valueOf(character.get(CharStats.PERCEPTION)), 106, 16, 20, true, consumer, CalculatorWindow::digit);
        stat_inputs[3] = new InputElement<>(gui, String.valueOf(character.get(CharStats.INTELLIGENCE)), 22, 26, 20, true, consumer, CalculatorWindow::digit);
        stat_inputs[4] = new InputElement<>(gui, String.valueOf(character.get(CharStats.DETERMINATION)), 64, 26, 20, true, consumer, CalculatorWindow::digit);
        stat_inputs[5] = new InputElement<>(gui, String.valueOf(character.get(CharStats.SPIRIT)), 106, 26, 20, true, consumer, CalculatorWindow::digit);
        mod_input = new InputElement<>(gui, "", 148, 21, 20, true, consumer, CalculatorWindow::digit);

        for (InputElement ie : stat_inputs) addElement(ie);
        addElement(mod_input);

        addElement(new LabelElement<>(gui, "STR", 2, 18));
        addElement(new LabelElement<>(gui, "REF", 44, 18));
        addElement(new LabelElement<>(gui, "PER", 86, 18));
        addElement(new LabelElement<>(gui, "MOD", 128, 23));
        addElement(new LabelElement<>(gui, "INT", 2, 29));
        addElement(new LabelElement<>(gui, "DET", 44, 29));
        addElement(new LabelElement<>(gui, "SPR", 86, 29));

        addElement(new ButtonElement<>(gui, "Физическая атака", 2, 40, 85, 20, b -> {
            move(ActionType.PHYSICAL);
            return true;
        }));
        addElement(new ButtonElement<>(gui, "Выстрел", 93, 40, 85, 20, b -> {
            move(ActionType.SHOOT);
            return true;
        }));
        addElement(new ButtonElement<>(gui, "Защита", 2, 62, 85, 20, b -> {
            move(ActionType.DEFEND);
            return true;
        }));
        addElement(new ButtonElement<>(gui, "Магия", 93, 62, 85, 20, b -> {
            move(ActionType.MAGIC);
            return true;
        }));
        addElement(new ButtonElement<>(gui, "Бросить d10 + MOD", 2, 84, 176, 20, b -> {
            if (mod_input.getText().isEmpty()) return true;

            consumer.accept(mod_input);
            int mod = mod_input.getText().isEmpty() ? 0 : Integer.valueOf(mod_input.getText());
            if (mod == 0) return true;

            MoveMessage message = new MoveMessage(MoveMessage.Type.CUSTOM);
            message.mod = mod;
            Misca.crabs.network.sendToServer(message);
            return true;
        }));
    }

    private static boolean digit(int key) {
        return (key >= Keyboard.KEY_1 && key <= Keyboard.KEY_0)
                /// Numpad keys
                || (key >= 0x47 && key <= 0x49) || (key >= 0x4B && key <= 0x4D) || (key >= 0x4F && key <= 0x52);
    }

    private static Character loadChar() {
        File file = new File(ConfigManager.config_dir, "calc_char.dat");
        if (!file.canRead()) return Character.makeDummy();

        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Character c = (Character) ois.readObject();
            fis.close();
            return c;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            file.delete();
            return Character.makeDummy();
        }
    }

    private void move(ActionType actionType) {
        assembleChar();

        consumer.accept(mod_input);
        int mod = mod_input.getText().isEmpty() ? 0 : Integer.valueOf(mod_input.getText());

        MoveMessage message = new MoveMessage(MoveMessage.Type.ACTION);
        message.character = character;
        message.actionType = actionType;
        message.mod = mod;

        Misca.crabs.network.sendToServer(message);
        saveChar();
    }

    @Override
    protected boolean onWindowClose() {
        saveChar();
        return super.onWindowClose();
    }

    private void assembleChar() {
        for (int i = 0; i < 6; i++) {
            InputElement input = stat_inputs[i];
            if (input.getText().isEmpty()) continue;
            consumer.accept(input);
            character.stats.put(CharStats.values()[i], Byte.valueOf(input.getText()));
        }
    }

    private void saveChar() {
        File file = new File(ConfigManager.config_dir, "calc_char.dat");
        if (!file.canWrite()) return;

        assembleChar();

        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(character);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class TwoDigitConsumer implements Consumer<InputElement<T>> {
        @Override
        public void accept(InputElement inputElement) {
            int len = inputElement.getText().length();
            if (len > 2) {
                int cur = inputElement.getText().substring(0, 2).equals("10") ? 2 : 1;
                inputElement.setCursorPosition(cur);
                inputElement.deleteFromCursor(len - cur);
            }
        }
    }
}
