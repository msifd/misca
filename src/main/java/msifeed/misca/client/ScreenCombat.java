package msifeed.misca.client;

import msifeed.mellow.MellowScreen;
import msifeed.mellow.utils.Direction;
import msifeed.mellow.utils.UiBuilder;
import msifeed.mellow.view.button.ButtonLabel;
import msifeed.mellow.view.text.Label;
import msifeed.mellow.view.text.TextInput;
import msifeed.misca.combat.CharAttribute;
import msifeed.misca.combat.cap.CombatantSync;
import net.minecraft.entity.EntityLivingBase;

import java.util.EnumMap;
import java.util.stream.Stream;

public class ScreenCombat extends MellowScreen {
    private final EntityLivingBase target;
    private final EnumMap<CharAttribute, TextInput> attrInputs = new EnumMap<>(CharAttribute.class);

    public ScreenCombat(EntityLivingBase target) {
        this.target = target;

        for (CharAttribute attr : CharAttribute.values()) {
            final TextInput input = new TextInput();
            input.grow(21, 0);
            input.getBackend().setMaxWidth(21);
            input.insert(Integer.toString(attr.getBase(target)));

            attrInputs.put(attr, input);
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        final int lineHeight = attrInputs.get(CharAttribute.str).getBaseGeom().h;

        UiBuilder.of(container)
                .add(new Label("Combat: " + target.getName())).center(Direction.HORIZONTAL)

                .beginGroup()
                    .forEach(CharAttribute.values(), (ui, attr) ->
                        ui.beginGroup()
                        .add(new Label(attr.name())).size(20, lineHeight).below().move(0, 3, 0)
                        .add(attrInputs.get(attr)).right().move(0, -1, 0)
                        .centerGroup(Direction.HORIZONTAL)
                        .endGroup())
                    .moveGroup(0, 10, 0)
                    .endGroup()

                .add(() -> {
                    final ButtonLabel btn = new ButtonLabel("[Submit]");
                    btn.setSize(50, 15);
                    btn.setCallback(this::submit);
                    return btn;
                }).below().move(0, 10, 0).center(Direction.HORIZONTAL)

                .centerGroup(Direction.BOTH)
                .apply(ui -> ui.moveGroup(0, -ui.getGroupContent().h * 2 / 3, 0))
                .build();
    }

    private void submit() {
        final int[] attrs = Stream.of(CharAttribute.values())
                .mapToInt(this::getAttrInput)
                .toArray();
        CombatantSync.postAttrs(target, attrs);
    }

    private int getAttrInput(CharAttribute attr) {
        final TextInput input = attrInputs.get(attr);
        try {
            return Integer.parseUnsignedInt(input.getText());
        } catch (Exception e) {
            return attr.getBase(target);
        }
    }
}
