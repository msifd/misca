package msifeed.misca.client;

import msifeed.mellow.MellowScreen;
import msifeed.mellow.utils.Direction;
import msifeed.mellow.utils.UiBuilder;
import msifeed.mellow.view.button.ButtonLabel;
import msifeed.mellow.view.text.LabelTr;
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
    final LabelTr chanceLabel = new LabelTr("gui.misca.efforts.chance", getChance());

    public ScreenEffortRoll(EntityPlayer target) {
        this.target = target;
        this.charsheet = CharsheetProvider.get(target);
        this.state = CharstateProvider.get(target);

        difficultyInput.setCallback(() -> chanceLabel.setText("gui.misca.efforts.chance", getChance()));
        difficultyInput.grow(30, 0);
        difficultyInput.insert("2");

        amountInput.setCallback(() -> chanceLabel.setText("gui.misca.efforts.chance", getChance()));
        amountInput.grow(30, 0);
        amountInput.insert("1");
    }

    @Override
    public void initGui() {
        super.initGui();

        UiBuilder.of(container)
                .add(new LabelTr("gui.misca.efforts", target.getDisplayNameString())).center(Direction.HORIZONTAL)

                .beginGroup()

                .beginGroup()
                    .add(new LabelTr("gui.misca.efforts.diff")).size(50, difficultyInput.getBaseGeom().h).below().move(0, 4, 0)
                    .add(difficultyInput).right().move(0, -2, 0)
                    .moveGroup(0, 10, 0)
                    .pinGroup()

                .beginGroup()
                    .add(new LabelTr("gui.misca.efforts.amount")).size(50, amountInput.getBaseGeom().h).below().move(0, 4, 0)
                    .add(amountInput).right().move(0, -2, 0)
                    .add(chanceLabel).size(30, amountInput.getBaseGeom().h).right().move(2, 1, 0)
//                    .centerGroup(Direction.HORIZONTAL)
                    .pinGroup()

                .centerGroup(Direction.HORIZONTAL)
//                .moveGroup(0, 10, 0)
                .appendGroup()

                .beginGroup()
                    .forEach(CharEffort.values(), (ui, effort) -> {
                        final int available = (int) Math.floor(state.efforts().get(effort));
                        final int poolSize = charsheet.effortPools().get(effort);
                        final String label = String.format("%s %d/%d", effort.tr(), available, poolSize);
                        final ButtonLabel effBtn = new ButtonLabel(label);
                        effBtn.setSize(100, 15);
                        effBtn.setCallback(() -> effortRoll(effort));

                        final ButtonLabel diceBtn = new ButtonLabel("Dice");
                        diceBtn.setSize(30, 15);
                        diceBtn.setCallback(() -> diceRoll(effort));

                        ui.beginGroup()
                            .add(effBtn).below()
                            .add(diceBtn).right()
                            .moveGroup(0, 2, 0)
                            .pinGroup()
                            ;
                    })
                    .centerGroup(Direction.HORIZONTAL)
                    .moveGroup(0, 10, 0)
                    .pinGroup()

                .centerGroup(Direction.BOTH)
                .moveGroup(0, -30, 0)
                .build();
    }

    private void effortRoll(CharEffort effort) {
        final int diff = getDifficulty();
        final int amount = getAmount();
        if (diff < 1 || amount < 1) return;

        RollRpc.doEffortRoll(effort, amount, diff);
    }

    private void diceRoll(CharEffort effort) {
        RollRpc.doEffortDice(effort);
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

    private int getChance() {
        final int diff = getDifficulty();
        final int amount = getAmount();
        if (diff > 0 && amount > 0)
            return (int) ((double) amount / diff * 100);
        else
            return 0;
    }
}
