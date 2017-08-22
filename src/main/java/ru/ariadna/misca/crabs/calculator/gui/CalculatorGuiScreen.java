package ru.ariadna.misca.crabs.calculator.gui;

import msifeed.mc.gui.WidgetGuiScreen;
import msifeed.mc.gui.events.MouseEvent;
import msifeed.mc.gui.layouts.BaseLayout;
import msifeed.mc.gui.layouts.EmptyLayout;
import msifeed.mc.gui.layouts.HorizontalLayout;
import msifeed.mc.gui.layouts.VerticalLayout;
import msifeed.mc.gui.widgets.*;
import ru.ariadna.misca.Misca;
import ru.ariadna.misca.MiscaUtils;
import ru.ariadna.misca.config.ConfigManager;
import ru.ariadna.misca.crabs.calculator.MoveMessage;
import ru.ariadna.misca.crabs.characters.CharStats;
import ru.ariadna.misca.crabs.characters.Character;
import ru.ariadna.misca.crabs.combat.parts.ActionType;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CalculatorGuiScreen extends WidgetGuiScreen {
    private static Character character = loadChar();
    private int mod = 0;

    public CalculatorGuiScreen() {
        List<IWidget> ws = new ArrayList<>();

        PanelWidget panel = new PanelWidget(170, 88);
        panel.setZLevel(-10);
        ws.add(panel);

        LabelWidget title = new LabelWidget(MiscaUtils.l10n("misca.crabs.calc"));
        title.setPosX(2);
        title.setPosY(2);
        ws.add(title);
        int title_offset = 12;

        for (int i = 0; i < CharStats.values().length; i++) {
            int row = i / 3, col = i % 3;
            StatLabelWidget label = new StatLabelWidget(CharStats.values()[i]);
            label.setPosX(2 + col * 42);
            label.setPosY(title_offset + row * 20 + 5);
            StatInputWidget input = new StatInputWidget(CharStats.values()[i]);
            input.setPosX(2 + col * 42 + 20);
            input.setPosY(title_offset + row * 20);
            ws.add(label);
            ws.add(input);
        }
        StatLabelWidget mod_label = new StatLabelWidget(null);
        mod_label.setPosX(2 + 3 * 42);
        mod_label.setPosY(title_offset + 15);
        StatInputWidget mod_input = new StatInputWidget(null);
        mod_input.setPosX(2 + 3 * 42 + 20);
        mod_input.setPosY(title_offset + 10);
        ws.add(mod_label);
        ws.add(mod_input);
        int stats_offset = title_offset + (CharStats.values().length / 3) * 20;

        for (int i = 0; i < ActionType.values().length; i++) {
            int row = i / 2, col = i % 2;
            ActionButtonWidget btn = new ActionButtonWidget(ActionType.values()[i]);
            btn.setPosX(2 + col * 84);
            btn.setPosY(stats_offset + row * 18);
            ws.add(btn);
        }

        IWidget[] arr = new IWidget[ws.size()];
        ws.toArray(arr);
        BaseLayout layout = new EmptyLayout().bind(arr);
        bindChild(new HorizontalLayout().bind(new VerticalLayout().bind(layout)));
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

    private static String statName(CharStats stat) {
        if (stat == null) return "MOD";
        switch (stat) {
            case SPIRIT:
                return "SPR";
            default:
                return stat.toString().substring(0, 3).toUpperCase();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        saveChar();
    }

    private void saveChar() {
        File file = new File(ConfigManager.config_dir, "calc_char.dat");

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

    private class StatLabelWidget extends LabelWidget {
        private CharStats stat;

        public StatLabelWidget(CharStats stat) {
            super(statName(stat));
            this.stat = stat;
        }

        @Override
        public void onMouseEvent(MouseEvent event) {
            if (stat == null || event.type != MouseEvent.Type.PRESS) return;
            setFocused();

            MoveMessage message = new MoveMessage(MoveMessage.Type.CUSTOM);
            message.stat = stat;
            message.stat_value = character.stats.get(stat);
            message.mod = mod;
            Misca.crabs.network.sendToServer(message);
        }
    }

    private class StatInputWidget extends TextInputWidget {
        private CharStats stat;

        public StatInputWidget(CharStats stat) {
            super(20, 16);
            this.stat = stat;
            if (stat != null) setText(String.valueOf(character.stats.get(stat)));
        }

        @Override
        public boolean onTextUpdate(String newText) {
            boolean ismod = stat == null;
            if (newText.isEmpty() || (ismod && newText.equals("-"))) return true;

            try {
                int val = Integer.valueOf(newText);
                boolean ok = ismod ? (val >= -20 && val <= 20) : (val > 0 && val < 11);
                if (ok) {
                    if (ismod) mod = val;
                    else character.stats.put(stat, (byte) val);
                }
                return ok;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        @Override
        public void onFocusLoose() {
            if (stat != null && text.isEmpty()) setText("1");
        }
    }

    private class ActionButtonWidget extends ButtonWidget {
        private ActionType actionType;

        public ActionButtonWidget(ActionType actionType) {
            super(actionType.toPrettyString(), 82, 16);
            this.actionType = actionType;
        }

        @Override
        protected void onPress(MouseEvent event) {
            MoveMessage message = new MoveMessage(MoveMessage.Type.ACTION);
            message.character = character;
            message.actionType = actionType;
            message.mod = mod;

            Misca.crabs.network.sendToServer(message);
            saveChar();
        }
    }
}
