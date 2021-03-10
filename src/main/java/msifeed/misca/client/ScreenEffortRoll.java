package msifeed.misca.client;

import msifeed.mellow.MellowScreen;
import msifeed.mellow.utils.Direction;
import msifeed.mellow.utils.UiBuilder;
import msifeed.mellow.view.button.ButtonLabel;
import msifeed.mellow.view.text.Label;
import msifeed.misca.charsheet.CharEffort;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.rolls.RollRpc;
import net.minecraft.entity.player.EntityPlayer;

public class ScreenEffortRoll extends MellowScreen {
    private final EntityPlayer target;
    private final ICharsheet charsheet;

    public ScreenEffortRoll(EntityPlayer target) {
        this.target = target;
        this.charsheet = CharsheetProvider.get(target);
    }

    @Override
    public void initGui() {
        super.initGui();

        container.clearViews();

        UiBuilder.of(container)
                .add(new Label("Efforts of " + target.getName())).at(0, 10).center(Direction.HORIZONTAL)

                .forEach(CharEffort.values(), (ui, effort) -> {
                    final String label = String.format("[%s (%d)]", effort.toString(), charsheet.effortPools().get(effort));
                    final ButtonLabel btn = new ButtonLabel(label);
                    btn.setCallback(() -> RollRpc.doEffortRoll(target, effort, 0));

                    ui.add(btn).below().move(0, 2, 0);
                })

                .centerGroup(Direction.HORIZONTAL)
                .build();
    }
}
