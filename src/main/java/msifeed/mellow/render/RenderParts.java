package msifeed.mellow.render;

import msifeed.mellow.utils.Geom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

import java.util.Collection;

public final class RenderParts {
//    private static boolean croppingActive = false;
//    private static boolean croppingPaused = false;
//
//    public static void beginCropped(Geom geom) {
//        final Minecraft mc = Minecraft.getMinecraft();
//        final int sf = RenderUtils.getScaledResolution().getScaleFactor();
//        final int x = geom.x * sf;
//        final int y = geom.y * sf;
//        final int w = geom.w * sf;
//        final int h = geom.h * sf;
//        GL11.glEnable(GL11.GL_SCISSOR_TEST);
//        GL11.glScissor(x, mc.displayHeight - h - y, w, h);
////        RenderShapes.rect(new Geom(x, mc.displayHeight - h - y, w, h), 0, 0);
//        croppingActive = true;
//    }
//
//    public static void endCropped() {
//        GL11.glDisable(GL11.GL_SCISSOR_TEST);
//        croppingActive = false;
//    }
//
//    public static void toggleCropping() {
//        if (!croppingActive)
//            return;
//        if (croppingPaused)
//            GL11.glEnable(GL11.GL_SCISSOR_TEST);
//        else
//            GL11.glDisable(GL11.GL_SCISSOR_TEST);
//        croppingPaused = !croppingPaused;
//    }
//
//    public static void cropped(Widget widget, Geom geom) {
//        beginCropped(geom);
//        widget.render();
//        endCropped();
//    }

//    public static void string(Geom geom, String text, int color) {
//        string(text, geom.x, geom.y, geom.z, color);
//    }

    public static void string(String text, Geom geom, int color, TextPref pref) {
        final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, geom.z);

        fr.drawString(text, geom.x + pref.xOff, geom.y + pref.yOff, color);

        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPopMatrix();
    }

    public static void lines(Collection<String> lines, Geom geom, int color, TextPref pref) {
        final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, geom.z);

        int lineOff = 0;
        if (pref.shadow) {
            for (String text : lines) {
                fr.drawStringWithShadow(text, geom.x + pref.xOff, geom.y + pref.yOff + lineOff, color);
                lineOff += fr.FONT_HEIGHT + pref.gap;
            }
        } else {
            for (String text : lines) {
                fr.drawString(text, geom.x + pref.xOff, geom.y + pref.yOff + lineOff, color);
                lineOff += fr.FONT_HEIGHT + pref.gap;
            }
        }

        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPopMatrix();
    }

    public static class TextPref {
        public boolean shadow = false;
        public int xOff, yOff;
        public int gap = 1;
    }
}
