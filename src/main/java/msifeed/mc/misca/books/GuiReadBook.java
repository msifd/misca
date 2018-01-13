package msifeed.mc.misca.books;

import msifeed.mc.gui.ImStyle;
import msifeed.mc.gui.NimGui;
import msifeed.mc.gui.font.FontFactory;
import msifeed.mc.gui.render.TextureInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.opengl.GL11;
import thvortex.betterfonts.StringCache;

import java.util.ArrayList;

public class GuiReadBook extends GuiScreen {
    private static final StringCache carefreeFont = FontFactory.createFontRenderer("Carefree.ttf", 28, true);
    private static final StringCache caslonFont = FontFactory.createFontRenderer("Caslon.ttf", 20, true);
    private static final StringCache vollkornFont = FontFactory.createFontRenderer("Vollkorn.ttf", 16, true);

    private RemoteBook book;
    private TextureInfo[] buttonTextures = new TextureInfo[2];

    private String[] lines;
    private int page = 0;

    public GuiReadBook(ItemStack itemStack) {
        // Try load book
        if (!itemStack.hasTagCompound()) return;
        final NBTTagCompound tc = itemStack.getTagCompound();

        final String name = tc.getString("name");
        if (name.isEmpty()) {
            closeGui();
            return;
        }

        final RemoteBook.Style style;
        try {
            style = RemoteBook.Style.valueOf(tc.getString("style"));
        } catch (Exception ignored) {
            return;
        }

        RemoteBookManager.INSTANCE.fetchBook(name, b -> {
            book = b;

            if (book.style == null) book.style = style;
            buttonTextures[0] = new TextureInfo(book.style.texture, 0, 192, 23, 13);
            buttonTextures[1] = new TextureInfo(book.style.texture, 23, 192, 23, 13);

            buildPages(book.text);
        });
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float tick) {
        if (book == null || lines == null) return;

        final int bgTextureWidth = 192;
        final int bgTextureHeight = 192;

        final int xOffset = (this.width - bgTextureWidth) / 2;

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(book.style.texture);
        this.drawTexturedModalRect(xOffset, 2, 0, 0, bgTextureWidth, bgTextureHeight);


        final NimGui nimgui = NimGui.INSTANCE;

        // Render text
        nimgui.imStyle.labelFont = getFontForStyle();

        int fontHeight = nimgui.imLabel.labelHeight();
        if (book.style == RemoteBook.Style.RICH_BOOK) fontHeight += 1; // Костыль для шрифта

        final int linesOnPage = 139 / fontHeight;
        final int startLine = page * linesOnPage;
        final int linesToDisplay = Math.min(linesOnPage, lines.length - startLine);

        for (int i = 0; i < linesToDisplay; i++) {
            final int yOffset = 10 + fontHeight + i * fontHeight;
            nimgui.imLabel.label(lines[startLine + i], xOffset + 34, yOffset, 0xFF000000, false);
        }

        // Page counter
        nimgui.imLabel.label(Integer.toString(page + 1), xOffset, 156, bgTextureWidth, 13, 0xFF000000, true, false);

        nimgui.imStyle.labelFont = ImStyle.DEFAULT.labelFont;

        // // //

        // Prev button
        if (page > 0 && nimgui.imButton.button(buttonTextures[1], xOffset + 38, 156, 23, 13)) {
            page--;
        }

        // Next button
        final boolean hasMorePages = (page + 1) * linesOnPage < lines.length;
        if (hasMorePages && nimgui.imButton.button(buttonTextures[0], xOffset + 120, 156, 23, 13)) {
            page++;
        }
    }

    private void buildPages(String text) {
        final int bookWidth = 115;

        final ArrayList<String> lines = new ArrayList<>();
        final String cleanedText = text.replaceAll("\r", "");
        final StringCache font = NimGui.INSTANCE.imStyle.labelFont;
        final StringBuilder sb = new StringBuilder(cleanedText);

        while (sb.length() > 0) {
            final int endBound = Math.min(bookWidth, sb.length());
            final int lineWidth;

            final String textPart = sb.substring(0, endBound);
            final boolean nextCharEmpty = textPart.charAt(0) == ' ' || textPart.charAt(0) == '\n';
            boolean skipLine = textPart.startsWith("\n\n");

            if (textPart.charAt(0) == '\n') {
                lineWidth = 1;
            } else {
                lineWidth = font.sizeStringToWidth(textPart, bookWidth);
            }

            final String line = textPart.substring((nextCharEmpty ? 1 : 0), lineWidth);

            if (!skipLine)
                lines.add(line);
            sb.delete(0, lineWidth);
        }

        this.lines = new String[lines.size()];
        lines.toArray(this.lines);
    }

    private StringCache getFontForStyle() {
        switch (book.style) {
            case BOOK:
                return caslonFont;
            case RICH_BOOK:
                return vollkornFont;
//            case PAD:
//                return alienFont;
            case NOTE:
                return carefreeFont;
            default:
                return FontFactory.fsexFont;
        }
    }

    private static void closeGui() {
        Minecraft.getMinecraft().displayGuiScreen(null);
    }
}
