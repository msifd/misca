package msifeed.misca.combat.client;

import msifeed.misca.combat.rpc.ICombatRpc;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class GuiScreenCombat extends GuiScreen {

    @Override
    public void initGui() {
        buttonList.clear();
        buttonList.add(new GuiButton(0, 10, 10, "Candidate"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                ICombatRpc.candidate();
                break;
        }
    }
}
