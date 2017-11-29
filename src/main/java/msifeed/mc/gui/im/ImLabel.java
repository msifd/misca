package msifeed.mc.gui.im;

import msifeed.mc.gui.font.FontFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.profiler.Profiler;
import thvortex.betterfonts.StringCache;

public class ImLabel {
    public StringCache font = FontFactory.fsexFont;

    /**
     * Draw label at pos
     */
    public void label(String label, int x, int y, int color, boolean shadow) {
        Profiler profiler = Minecraft.getMinecraft().mcProfiler;
        profiler.startSection("ImLabel");
        font.renderString(label, x, y, color, shadow);
        profiler.endSection();
    }

    /**
     * Splits lines at line break and draws
     */
    public void multiline(String label, int x, int y, int color, boolean shadow) {
        final int labelHeight = (font.getFontSize() - 1) / 2; // ???
        final String[] lines = label.split("\n");
        for (int i = 0; i < lines.length; i++) {
            label(lines[i], x, y + i * labelHeight, color, shadow);
        }
    }

    /**
     * Draw centered in rect label
     * @param trim label to fit in rect
     */
    public void label(String label, int x, int y, int width, int height, int color, boolean shadow, boolean trim) {
        final String str;
        if (trim) str = font.trimStringToWidth(label, width, false);
        else str = label;
        final int labelWidth = font.getStringWidth(str)
                , labelHeight = (font.getFontSize() - 1) / 2; // ???
        final int paddingX = (width - labelWidth) / 2
                , paddingY = (height - labelHeight) / 2;

        label(str, x + paddingX, y + paddingY, color, shadow);
    }
}
