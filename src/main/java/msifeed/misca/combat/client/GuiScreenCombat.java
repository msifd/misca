package msifeed.misca.combat.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;

import java.io.IOException;

public class GuiScreenCombat extends GuiScreen {
    private final EntityLivingBase entity;

    public GuiScreenCombat(EntityLivingBase entity) {
        this.entity = entity;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        buttonList.add(new GuiButton(0, 10, 10, "Candidate"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                break;
        }
    }
}
