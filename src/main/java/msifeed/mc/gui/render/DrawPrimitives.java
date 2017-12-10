package msifeed.mc.gui.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

public class DrawPrimitives {

    public static void drawRect(int x1, int y1, int x2, int y2, int color) {
        drawRect(x1, y1, x2, y2, 0., color);
    }

    public static void drawRect(int x1, int y1, int x2, int y2, double z, int color) {
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
        tessellator.addVertex((double) x1, (double) y2, z);
        tessellator.addVertex((double) x2, (double) y2, z);
        tessellator.addVertex((double) x2, (double) y1, z);
        tessellator.addVertex((double) x1, (double) y1, z);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void drawTexture(TextureInfo tex, int x, int y, double z) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(tex.resource);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedRect(x, y, z, tex.u, tex.v, tex.width, tex.height);
    }

    public static void drawTexture(TextureInfo tex, int x, int y, double z, int offsetV) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(tex.resource);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedRect(x, y, z, tex.u, tex.v + offsetV, tex.width, tex.height);
    }

    public static void drawRepeatedTexture(TextureInfo tex, int x, int y, double z, int width, int height) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(tex.resource);

//        GL11.glEnable(GL11.GL_BLEND);
//        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
//        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        final double f = 0.00390625F;
        final double f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double) (x + 0), (double) (y + height), z, (float) (tex.u + 0) * f, (float) (tex.v + tex.height) * f1);
        tessellator.addVertexWithUV((double) (x + width), (double) (y + height), z, (float) (tex.u + tex.width) * f, (float) (tex.v + tex.height) * f1);
        tessellator.addVertexWithUV((double) (x + width), (double) (y + 0), z, (float) (tex.u + tex.width) * f, (float) (tex.v + 0) * f1);
        tessellator.addVertexWithUV((double) (x + 0), (double) (y + 0), z, (float) (tex.u + 0) * f, (float) (tex.v + 0) * f1);
        tessellator.draw();

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);

//        GL11.glDisable(GL11.GL_BLEND);
//        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    public static void drawTexturedRect(int x, int y, double z, int u, int v, int width, int height) {
        final float f = 0.00390625F;
        final float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double) (x + 0), (double) (y + height), z, (double) ((float) (u + 0) * f), (double) ((float) (v + height) * f1));
        tessellator.addVertexWithUV((double) (x + width), (double) (y + height), z, (double) ((float) (u + width) * f), (double) ((float) (v + height) * f1));
        tessellator.addVertexWithUV((double) (x + width), (double) (y + 0), z, (double) ((float) (u + width) * f), (double) ((float) (v + 0) * f1));
        tessellator.addVertexWithUV((double) (x + 0), (double) (y + 0), z, (double) ((float) (u + 0) * f), (double) ((float) (v + 0) * f1));
        tessellator.draw();
    }

    public static void drawInvertedRect(int x, int y, int z, int width, int height, int color) {
        final float alpha = (float) (color >> 24 & 255) / 255.0F;
        final float red = (float) (color >> 16 & 255) / 255.0F;
        final float green = (float) (color >> 8 & 255) / 255.0F;
        final float blue = (float) (color & 255) / 255.0F;

        GL11.glColor4f(red, green, blue, alpha);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glLogicOp(GL11.GL_OR_REVERSE);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertex(x, y + height, z);
        tessellator.addVertex(x + width, y + height, z);
        tessellator.addVertex(x + width, y, z);
        tessellator.addVertex(x, y, z);
        tessellator.draw();

        GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
}
