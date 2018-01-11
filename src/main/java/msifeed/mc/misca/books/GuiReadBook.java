package msifeed.mc.misca.books;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class GuiReadBook extends GuiScreen {
    private RemoteBook book = new RemoteBook();

    public GuiReadBook(ItemStack itemStack) {
        // Try load book
        if (!itemStack.hasTagCompound()) return;
        final NBTTagCompound tc = itemStack.getTagCompound();

        book.title = tc.getString("name");
        if (book.title.isEmpty()) {
            closeGui();
            return;
        }

        try {
            book.style = RemoteBook.Style.valueOf(tc.getString("style"));
        } catch (Exception ignored) {
            return;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float tick) {

    }

    private static void closeGui() {
        Minecraft.getMinecraft().displayGuiScreen(null);
    }
}
