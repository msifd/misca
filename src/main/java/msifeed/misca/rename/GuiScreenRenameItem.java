package msifeed.misca.rename;

import msifeed.misca.rename.RenameRpc;
import msifeed.sys.gui.GuiMultilineTextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiScreenRenameItem extends GuiScreen {
    private final ItemStack stack;
    private GuiTextField titleEdit;
    private GuiMultilineTextField descEdit;

    public GuiScreenRenameItem(ItemStack stack) {
        this.stack = stack.copy();
    }

    @Override
    public void initGui() {
        final int xOff = width / 2 - 150;
        final int btnYOff = fontRenderer.FONT_HEIGHT * 8 + 130 + 10;

        buttonList.clear();
        buttonList.add(new GuiButton(0,  xOff, btnYOff, 80, 20, "Submit"));
        buttonList.add(new GuiButton(1,  xOff + 110, btnYOff, 80, 20, "Cancel"));
        buttonList.add(new GuiButton(2,  xOff + 220, btnYOff, 80, 20, "Clear"));

        titleEdit = new GuiTextField(3, fontRenderer, xOff, 110, 300, 20);
        titleEdit.setText(intoAmpersands(stack.getDisplayName()));
        titleEdit.setGuiResponder(new GuiPageButtonList.GuiResponder() {
            public void setEntryValue(int id, boolean value) {
            }
            public void setEntryValue(int id, float value) {
            }
            public void setEntryValue(int id, String value) {
                stack.setStackDisplayName(fromAmpersands(value));
            }
        });

        descEdit = new GuiMultilineTextField(4, fontRenderer, xOff, 130, 300, 8);
        descEdit.setLines(getStackDesc());
        descEdit.setCallback(this::updateStackDesc);
    }

    @Override
    public void updateScreen() {
        titleEdit.updateCursorCounter();
        descEdit.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        renderToolTip(stack, width / 2 - 150 - 9, 20);
        RenderHelper.enableGUIStandardItemLighting();

        titleEdit.drawTextBox();
        descEdit.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (!button.enabled) return;

        switch (button.id) {
            case 0:
                RenameRpc.sendRename(stack);
                Minecraft.getMinecraft().displayGuiScreen(null);
                break;
            case 1:
                Minecraft.getMinecraft().displayGuiScreen(null);
                break;
            case 2:
                final NBTTagCompound display = stack.getSubCompound("display");
                if (display != null)
                    display.removeTag("Lore");
                stack.clearCustomName();

                titleEdit.setText(intoAmpersands(stack.getDisplayName()));
                descEdit.setLines(getStackDesc());
                break;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        titleEdit.textboxKeyTyped(typedChar, keyCode);
        descEdit.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        titleEdit.mouseClicked(mouseX, mouseY, mouseButton);
        descEdit.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private List<String> getStackDesc() {
        final NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null || !nbt.hasKey("display", 10)) return Collections.emptyList();

        final NBTTagCompound display = nbt.getCompoundTag("display");
        if (display.getTagId("Lore") != 9) return Collections.emptyList();

        final NBTTagList tagList = display.getTagList("Lore", 8);
        if (tagList.isEmpty()) return Collections.emptyList();

        final List<String> lines = new ArrayList<>();
        for (int i = 0; i < tagList.tagCount(); ++i)
            lines.add(intoAmpersands(tagList.getStringTagAt(i)));

        return lines;
    }

    private void updateStackDesc(List<String> lines) {
        final NBTTagCompound display = stack.getOrCreateSubCompound("display");

        final NBTTagList tagList = new NBTTagList();
        for (String s : lines)
            tagList.appendTag(new NBTTagString(fromAmpersands(s)));

        display.setTag("Lore", tagList);
    }

    private static String intoAmpersands(String str) {
        return str.replace('\u00A7', '&');
    }

    private static String fromAmpersands(String str) {
        return str.replace('&', '\u00A7');
    }
}
