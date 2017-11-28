package msifeed.mc.gui.render;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

public class DrawPrimitives {
    public static void drawRect(int x1, int y1, int x2, int y2, int color) {
        int tmp;
        if (x1 < x2) {
            tmp = x1;
            x1 = x2;
            x2 = tmp;
        }
        if (y1 < y2) {
            tmp = y1;
            y1 = y2;
            y2 = tmp;
        }

        final float alpha = (float) (color >> 24 & 255) / 255.0F;
        final float red = (float) (color >> 16 & 255) / 255.0F;
        final float green = (float) (color >> 8 & 255) / 255.0F;
        final float blue = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(red, green, blue, alpha);
        tessellator.startDrawingQuads();
        tessellator.addVertex((double) x1, (double) y2, 0.0D);
        tessellator.addVertex((double) x2, (double) y2, 0.0D);
        tessellator.addVertex((double) x2, (double) y1, 0.0D);
        tessellator.addVertex((double) x1, (double) y1, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public void drawTexturedModalRect(int x, int y, int z, int u, int v, int width, int height) {
        final float f = 0.00390625F;
        final float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double) (x + 0), (double) (y + height), (double) z, (double) ((float) (u + 0) * f), (double) ((float) (v + height) * f1));
        tessellator.addVertexWithUV((double) (x + width), (double) (y + height), (double) z, (double) ((float) (u + width) * f), (double) ((float) (v + height) * f1));
        tessellator.addVertexWithUV((double) (x + width), (double) (y + 0), (double) z, (double) ((float) (u + width) * f), (double) ((float) (v + 0) * f1));
        tessellator.addVertexWithUV((double) (x + 0), (double) (y + 0), (double) z, (double) ((float) (u + 0) * f), (double) ((float) (v + 0) * f1));
        tessellator.draw();
    }
}
