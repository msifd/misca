package msifeed.misca.client;

import msifeed.mellow.MellowScreen;
import msifeed.mellow.utils.Direction;
import msifeed.mellow.utils.UiBuilder;
import msifeed.mellow.view.button.ButtonLabel;
import msifeed.mellow.view.text.Label;
import msifeed.mellow.view.text.TextInput;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.charsheet.cap.CharsheetSync;
import net.minecraft.entity.player.EntityPlayer;

public class ScreenCharsheet extends MellowScreen {
    private final EntityPlayer target;
    private final ICharsheet charsheet;

    final TextInput nameInput = new TextInput();
    final TextInput wikiInput = new TextInput();

    public ScreenCharsheet(EntityPlayer target) {
        this.target = target;
        this.charsheet = CharsheetProvider.get(target);

        nameInput.insert(charsheet.getName());
        nameInput.setSize(100, 10);
        nameInput.getBackend().getView().setSize(100, 10);
        nameInput.getBackend().setMaxLines(1);

        wikiInput.insert(charsheet.getWikiPage());
        wikiInput.setSize(100, 10);
        wikiInput.getBackend().getView().setSize(100, 10);
        wikiInput.getBackend().setMaxLines(1);
    }

    @Override
    public void initGui() {
        super.initGui();

        container.clearViews();

        try {

        UiBuilder.of(container)
                .add(new Label("Charsheet: " + target.getDisplayNameString())).center(Direction.HORIZONTAL)

                .beginGroup()
                    .add(new Label("Name")).groupBase().size(30, 11).below().move(0, 10, 0)
                    .add(nameInput).right()
                    .centerGroup(Direction.HORIZONTAL)
                    .endGroup()
                .beginGroup()
                    .add(new Label("Wiki")).groupBase().size(30, 11).below()
                    .add(wikiInput).right()
                    .centerGroup(Direction.HORIZONTAL)
                    .endGroup()

                .add(() -> {
                    final ButtonLabel btn = new ButtonLabel("[Submit]");
                    btn.setCallback(this::submit);
                    return btn;
                }).below().move(0, 10, 0).center(Direction.HORIZONTAL)

                .centerGroup(Direction.HORIZONTAL)
                .moveGroup(0, 30, 0)
                .build();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void submit() {
        charsheet.setName(nameInput.getText());
        charsheet.setWikiPage(wikiInput.getText());
        CharsheetSync.post(target, charsheet);
    }
}
