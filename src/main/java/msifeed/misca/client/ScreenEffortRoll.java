package msifeed.misca.client;

import msifeed.mellow.MellowScreen;
import msifeed.mellow.utils.Direction;
import msifeed.mellow.utils.UiBuilder;
import msifeed.mellow.view.button.ButtonLabel;
import msifeed.mellow.view.text.Label;
import msifeed.mellow.view.text.TextInput;
import msifeed.misca.charsheet.CharEffort;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.charstate.cap.CharstateProvider;
import msifeed.misca.charstate.cap.ICharstate;
import msifeed.misca.rolls.RollRpc;
import net.minecraft.entity.player.EntityPlayer;

public class ScreenEffortRoll extends MellowScreen {
    private final EntityPlayer target;
    private final ICharsheet charsheet;
    private final ICharstate state;

    final TextInput difficultyInput = new TextInput();
    final TextInput amountInput = new TextInput();

    public ScreenEffortRoll(EntityPlayer target) {
        this.target = target;
        this.charsheet = CharsheetProvider.get(target);
        this.state = CharstateProvider.get(target);

        difficultyInput.grow(30, 0);
        difficultyInput.insert("2");

        amountInput.grow(30, 0);
        amountInput.insert("1");
    }

    @Override
    public void initGui() {
        super.initGui();

        UiBuilder.of(container)
                .add(new Label("Efforts: " + target.getDisplayNameString())).center(Direction.HORIZONTAL)

                .beginGroup()
                    .add(new Label("Difficulty")).size(50, difficultyInput.getBaseGeom().h).below().move(0, 4, 0)
                    .add(difficultyInput).right().move(0, -2, 0)
                    .centerGroup(Direction.HORIZONTAL)
                    .moveGroup(0, 10, 0)
                    .pinGroup()

                .beginGroup()
                    .add(new Label("Amount")).size(50, amountInput.getBaseGeom().h).below().move(0, 4, 0)
                    .add(amountInput).right().move(0, -2, 0)
                    .centerGroup(Direction.HORIZONTAL)
                    .pinGroup()

                .beginGroup()
                    .forEach(CharEffort.values(), (ui, effort) -> {
                        final int available = (int) Math.floor(state.efforts().get(effort));
                        final int poolSize = charsheet.effortPools().get(effort);
                        final String label = String.format("%s %d/%d", effort.toString(), available, poolSize);
                        final ButtonLabel btn = new ButtonLabel(label);
                        btn.setSize(100, 15);
                        btn.setCallback(() -> roll(effort));

                        ui.add(btn).below().move(0, 2, 0);
                    })
                    .centerGroup(Direction.HORIZONTAL)
                    .moveGroup(0, 10, 0)
                    .pinGroup()

                .centerGroup(Direction.BOTH)
                .moveGroup(0, -30, 0)
                .build();
    }

    private void roll(CharEffort effort) {
        final int diff = getDifficulty();
        final int amount = getAmount();
        if (diff < 1 || amount < 1) return;

        RollRpc.doEffortRoll(target, effort, amount, diff);
    }

    private int getDifficulty() {
        try {
            return Integer.parseUnsignedInt(difficultyInput.getText());
        } catch (Exception e) {
            return 0;
        }
    }

    private int getAmount() {
        try {
            return Integer.parseUnsignedInt(amountInput.getText());
        } catch (Exception e) {
            return 0;
        }
    }
}
