package msifeed.mc.misca.books;

import msifeed.mc.gui.NimGui;
import msifeed.mc.gui.nim.NimText;
import msifeed.mc.gui.nim.NimWindow;
import msifeed.mc.misca.utils.MiscaNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiEditBook extends GuiScreen {
    private final NimWindow writerWindow = new NimWindow(I18n.format("misca.books.editor.title"), GuiEditBook::closeGui);
    private final NimText nameInput = new NimText();

    private boolean nameChecked = true;
    private long lastInput = System.currentTimeMillis();
    private CheckStatus checkStatus = null;

    public GuiEditBook() {
        nameInput.onUnfocus = s -> checkRemoteBook();
        nameInput.validateText = s -> {
            nameChecked = false;
            lastInput = System.currentTimeMillis();
            return true;
        };
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float tick) {
        // Чекаем после изменений
        if (!nameChecked && System.currentTimeMillis() - lastInput > 500) {
            checkRemoteBook();
        }

        final NimGui nimgui = NimGui.INSTANCE;
        nimgui.beginWindow(writerWindow);

        nimgui.horizontalBlock();
        nimgui.label(I18n.format("misca.books.editor.index"));
        nimgui.nim(nameInput);

        nimgui.verticalBlock();
        if (checkStatus != null) {
            final String status;
            if (checkStatus == CheckStatus.CHECKING) status = "...";
            else if (checkStatus == CheckStatus.EXISTS) status = I18n.format("misca.books.editor.exists");
            else status = I18n.format("misca.books.editor.missing");

            nimgui.label(I18n.format("misca.books.editor.the_book") + status);
        }

        if (nimgui.button(I18n.format("misca.books.editor.get")) && checkStatus == CheckStatus.EXISTS) {
            MiscaNetwork.INSTANCE.sendToServer(new MessageRemoteBook(MessageRemoteBook.Type.SIGN, nameInput.getText()));
            closeGui();
        }

        nimgui.endWindow();
    }

    private void checkRemoteBook() {
        final String name = nameInput.getText();
        if (nameChecked || name.isEmpty() || checkStatus == CheckStatus.CHECKING) return;

        nameChecked = true;
        checkStatus = CheckStatus.CHECKING;
        RemoteBookManager.INSTANCE.sendCheck(name, exists -> checkStatus = exists ? CheckStatus.EXISTS : CheckStatus.MISSING);
    }

    private static void closeGui() {
        Minecraft.getMinecraft().displayGuiScreen(null);
    }

    private enum CheckStatus {
        CHECKING, EXISTS, MISSING
    }
}
