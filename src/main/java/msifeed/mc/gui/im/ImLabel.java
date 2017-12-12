package msifeed.mc.gui.im;

import msifeed.mc.gui.ImGui;
import msifeed.mc.gui.ImStyle;
import msifeed.mc.gui.font.FontFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.profiler.Profiler;
import thvortex.betterfonts.StringCache;

public class ImLabel {
    public StringCache font = FontFactory.fsexFont;

    /**
     * Draw label at pos
     *
     * @return width
     */
    public int label(String label, int x, int y, int color, boolean shadow) {
        final ImStyle st = ImGui.INSTANCE.imStyle;
        Profiler profiler = Minecraft.getMinecraft().mcProfiler;
        profiler.startSection("ImLabel");
        final int width = font.renderString(label, x, y - 1, color, shadow); // y - 1 slightly fixes ypos
        profiler.endSection();
        return width;
    }

    /**
     * Splits lines at line break and draws
     *
     * @return width
     */
    public int[] multiline(String label, int x, int y, int color, boolean shadow) {
        final String[] lines = label.split("\n");
        int width = 0;
        for (int i = 0; i < lines.length; i++) {
            width += label(lines[i], x, y + i * labelHeight(), color, shadow);
        }
        return new int[]{width, lines.length * labelHeight()};
    }

    /**
     * Draw centered in rect label
     *
     * @param trim label to fit in rect
     */
    public int label(String label, int x, int y, int width, int height, int color, boolean centerWidth, boolean trim) {
        final String str;
        if (trim) str = font.trimStringToWidth(label, width, false);
        else str = label;
        final int labelWidth = font.getStringWidth(str), labelHeight = labelHeight();
        final int paddingX = centerWidth ? (width - labelWidth) / 2 : 0, paddingY = (height - labelHeight) / 2;

        return label(str, x + paddingX, y + paddingY, color, false);
    }

    public int labelHeight() {
        return (font.getFontSize() - 1) / 2;  // ???
    }
}
