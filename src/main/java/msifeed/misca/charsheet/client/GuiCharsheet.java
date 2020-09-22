package msifeed.misca.charsheet.client;

import msifeed.misca.charsheet.ICharsheetRpc;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.charsheet.cap.ICharsheet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public class GuiCharsheet extends GuiScreen {
    private final EntityPlayer player;
    private final ICharsheet charsheet;
    private GuiTextField nameEdit;

    public GuiCharsheet(EntityPlayer player) {
        this.player = player;
        this.charsheet = player.getCapability(CharsheetProvider.CAP, null);
    }

    @Override
    public void initGui() {
        buttonList.clear();
        buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 24 + 12, "Submit"));
        buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 48 + 12, "Cancel"));

        nameEdit = new GuiTextField(2, this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
        nameEdit.setText(charsheet.getName());
    }

    @Override
    public void updateScreen() {
        nameEdit.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        nameEdit.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (!button.enabled) return;

        switch (button.id) {
            case 0:
                charsheet.setName(nameEdit.getText());

                ICharsheetRpc.updateCharsheet(player, charsheet);
                Minecraft.getMinecraft().displayGuiScreen(null);
                break;
            case 1:
                Minecraft.getMinecraft().displayGuiScreen(null);
                break;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        nameEdit.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        nameEdit.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
