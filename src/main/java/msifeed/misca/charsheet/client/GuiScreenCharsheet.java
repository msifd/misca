package msifeed.misca.charsheet.client;

import msifeed.misca.charsheet.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;
import java.util.ArrayList;

public class GuiScreenCharsheet extends GuiScreen {
    private final EntityPlayer player;
    private final ICharsheet charsheet;

    private GuiTextField nameEdit;
    private GuiTextField wikiEdit;
    private final ArrayList<GuiTextField> attrFields = new ArrayList<>();
    private final ArrayList<GuiTextField> skillFields = new ArrayList<>();

    public GuiScreenCharsheet(EntityPlayer player) {
        this.player = player;
        this.charsheet = CharsheetProvider.get(player).clone();
    }

    @Override
    public void initGui() {
        buttonList.clear();
        buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 24 + 12, "Submit"));
        buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 48 + 12, "Cancel"));

        nameEdit = new GuiTextField(2, this.fontRenderer, this.width / 2 - 100, 50, 200, 20);
        nameEdit.setText(charsheet.getName());

        wikiEdit = new GuiTextField(3, this.fontRenderer, this.width / 2 - 100, 70, 200, 20);
        wikiEdit.setText(charsheet.getWikiPage());

        attrFields.clear();
        for (CharAttribute attr : CharAttribute.values()) {
            final GuiTextField f = new GuiTextField(10 + attr.ordinal(), fontRenderer, 10, 10 + attr.ordinal() * 22, 50, 20);
            f.setText(Integer.toString(charsheet.attrs().get(attr)));
            f.setValidator(input -> {
                try {
                    if (input == null) return false;
                    if (input.isEmpty()) return true;
                    final int i = Integer.parseUnsignedInt(input);
                    return i >= 0 && i <= 25;
                } catch (NumberFormatException e) {
                    return false;
                }
            });
            attrFields.add(f);
        }

        skillFields.clear();
        for (CharSkill skill : CharSkill.values()) {
            final GuiTextField f = new GuiTextField(10 + skill.ordinal(), fontRenderer, 65, 10 + skill.ordinal() * 22, 50, 20);
            f.setText(Integer.toString(charsheet.skills().get(skill)));
            f.setValidator(input -> {
                try {
                    if (input == null) return false;
                    if (input.isEmpty()) return true;
                    final int i = Integer.parseUnsignedInt(input);
                    return i >= 0 && i <= 25;
                } catch (NumberFormatException e) {
                    return false;
                }
            });
            skillFields.add(f);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (!button.enabled) return;

        switch (button.id) {
            case 0:
                charsheet.setName(nameEdit.getText().trim());
                charsheet.setWikiPage(wikiEdit.getText().trim());
                for (int i = 0; i < attrFields.size(); i++) {
                    final CharAttribute attr = CharAttribute.values()[i];
                    final String input = attrFields.get(i).getText().trim();
                    charsheet.attrs().set(attr, input.isEmpty() ? 0 : Integer.parseInt(input));
                }

                CharsheetSync.post(player, charsheet);
                Minecraft.getMinecraft().displayGuiScreen(null);
                break;
            case 1:
                Minecraft.getMinecraft().displayGuiScreen(null);
                break;
        }
    }

    @Override
    public void updateScreen() {
        nameEdit.updateCursorCounter();
        wikiEdit.updateCursorCounter();
        attrFields.forEach(GuiTextField::updateCursorCounter);
        skillFields.forEach(GuiTextField::updateCursorCounter);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        nameEdit.drawTextBox();
        wikiEdit.drawTextBox();
        attrFields.forEach(GuiTextField::drawTextBox);
        skillFields.forEach(GuiTextField::drawTextBox);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        nameEdit.textboxKeyTyped(typedChar, keyCode);
        wikiEdit.textboxKeyTyped(typedChar, keyCode);
        attrFields.forEach(f -> f.textboxKeyTyped(typedChar, keyCode));
        skillFields.forEach(f -> f.textboxKeyTyped(typedChar, keyCode));
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        nameEdit.mouseClicked(mouseX, mouseY, mouseButton);
        wikiEdit.mouseClicked(mouseX, mouseY, mouseButton);
        attrFields.forEach(f -> f.mouseClicked(mouseX, mouseY, mouseButton));
        skillFields.forEach(f -> f.mouseClicked(mouseX, mouseY, mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
