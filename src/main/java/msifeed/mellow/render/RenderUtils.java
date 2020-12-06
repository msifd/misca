package msifeed.mellow.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public final class RenderUtils {
    private static int cacheHint = 0;
    private static ScaledResolution scaledResolution = null;

    public static ScaledResolution getScaledResolution() {
        final Minecraft mc = Minecraft.getMinecraft();

        final int hint = mc.displayWidth + mc.displayHeight + (mc.isFullScreen() ? 1 : 2) * 100 + mc.gameSettings.guiScale * 100;
        if (scaledResolution == null || cacheHint != hint) {
            scaledResolution = new ScaledResolution(mc);
            cacheHint = hint;
        }

        return scaledResolution;
    }
}
