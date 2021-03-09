package msifeed.misca.client;

import msifeed.mellow.MellowScreen;
import msifeed.mellow.utils.Direction;
import msifeed.mellow.utils.UiBuilder;
import msifeed.mellow.view.button.ButtonLabel;
import msifeed.mellow.view.text.Label;
import msifeed.misca.charsheet.CharEffort;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.charsheet.cap.CharsheetSync;
import msifeed.misca.combat.CharAttribute;
import msifeed.misca.rolls.RollRpc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

public class ScreenCombat extends MellowScreen {
    private final EntityLivingBase target;

    public ScreenCombat(EntityLivingBase entity) {
        this.target = entity;
    }

    @Override
    public void initGui() {
        super.initGui();

        container.clearViews();

        UiBuilder.of(container)
                .add(new Label("Combat attrs of " + target.getName())).at(0, 10).center(Direction.HORIZONTAL)
                .forEach(Stream.of(CharAttribute.values()), (ui, attr) -> {
                    final String label = String.format("%s (%f)", attr.toString(), attr.get(target));
//                    final ButtonLabel btn = new ButtonLabel(new Label(label));
//                    btn.setCallback(() -> RollRpc.doEffortRoll(target, attr, 0));

                    ui.add(new Label(label)).below();
                })
                .build();
    }
}
