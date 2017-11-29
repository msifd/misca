package msifeed.mc.gui.font;

import thvortex.betterfonts.StringCache;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class FontFactory {
    private static int[] colorCodes = new int[32];
    public static final StringCache fsexFont;

    static {
        for (int i = 0; i < 32; ++i) {
            int j = (i >> 3 & 1) * 85;
            int k = (i >> 2 & 1) * 170 + j;
            int l = (i >> 1 & 1) * 170 + j;
            int i1 = (i >> 0 & 1) * 170 + j;

            if (i == 6) {
                k += 85;
            }

            if (i >= 16) {
                k /= 4;
                l /= 4;
                i1 /= 4;
            }

            colorCodes[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
        }

        fsexFont = FontFactory.createFsexFontRenderer();
    }

    /**
     * Fixedsys Excelsior 3
     */
    private static StringCache createFsexFontRenderer() {
        try {
            final InputStream is = FontFactory.class.getResourceAsStream("FSEX300.ttf");
            final Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            return createFontRenderer(font, 16, false);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static StringCache createFontRenderer(Font font, int size, boolean antiAlias) {
        final StringCache sc = new StringCache(colorCodes);
        sc.setDefaultFont(font, size, antiAlias);
        return sc;
    }
}
