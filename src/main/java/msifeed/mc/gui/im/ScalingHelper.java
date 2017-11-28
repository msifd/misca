package msifeed.mc.gui.im;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public class ScalingHelper {
    private static int latestWidth = 0;
    private static int latestHeight = 0;
    private static double scaleFactor = 1;

    public static int scaleWidth(int width) {
        checkFactor();
        return MathHelper.ceiling_double_int((double) width / scaleFactor);
    }

    public static int scaleHeight(int height) {
        checkFactor();
        return MathHelper.ceiling_double_int((double) height / scaleFactor);
    }

    public static int[] scale(int... args) {
        checkFactor();
        for (int i = 0; i < args.length; i++)
            args[i] = MathHelper.ceiling_double_int((double) args[i] / scaleFactor);
        return args;
    }

    public static int[] scaleBack(int... args) {
        checkFactor();
        for (int i = 0; i < args.length; i++)
            args[i] = MathHelper.ceiling_double_int((double) args[i] * scaleFactor);
        return args;
    }

    private static void checkFactor() {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.displayWidth == latestWidth && mc.displayHeight == latestHeight) return;

        latestWidth = mc.displayWidth;
        latestHeight = mc.displayHeight;
        scaleFactor = 1;

        final boolean isUnicode = mc.func_152349_b();
        int guiScale = mc.gameSettings.guiScale;
        if (guiScale == 0) guiScale = 1000;

        while (scaleFactor < guiScale && latestWidth / (scaleFactor + 1) >= 320 && latestHeight / (scaleFactor + 1) >= 240) {
            scaleFactor += 1;
        }

        if (isUnicode && scaleFactor % 2 != 0 && scaleFactor != 1) {
            scaleFactor -= 1;
        }
    }
}
