package msifeed.mellow.render;

import msifeed.mellow.utils.Geom;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public final class RenderShapes {
//    public static void line(Geom geom, int color, float thickness) {
//        GL11.glDisable(GL11.GL_TEXTURE_2D);
//        GL11.glLineWidth(thickness);
//        final Tessellator tessellator = Tessellator.instance;
//        tessellator.startDrawing(GL11.GL_LINES);
//        tessellator.setColorRGBA_I(color, 255);
//        tessellator.addVertex(geom.x, geom.y + 0.3, geom.z);
//        tessellator.addVertex(geom.x + geom.w, geom.y + geom.h + 0.3, geom.z);
//        tessellator.draw();
//        GL11.glEnable(GL11.GL_TEXTURE_2D);
//    }

    public static void frame(Geom geom, int color, float thickness) {
        final float a = (color >> 24 & 255) / 255f;
        final float r = (color >> 16 & 255) / 255f;
        final float g = (color >> 8 & 255) / 255f;
        final float b = (color & 255) / 255f;

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
        GlStateManager.color(r, g, b, a);
        GlStateManager.glLineWidth(thickness);

        buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
        buffer.pos(geom.x, geom.y + geom.h, geom.z);
        buffer.pos(geom.x + geom.w, geom.y + geom.h, geom.z);
        buffer.pos(geom.x + geom.w, geom.y, geom.z);
        buffer.pos(geom.x, geom.y, geom.z);
        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void rect(Geom geom, int color) {
        Gui.drawRect(geom.x, geom.y, geom.x + geom.w, geom.y + geom.h, color);
        if (false) return;


        final float a = (color >> 24 & 255) / 255f;
        final float r = (color >> 16 & 255) / 255f;
        final float g = (color >> 8 & 255) / 255f;
        final float b = (color & 255) / 255f;

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
        GlStateManager.color(r, g, b, a);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        buffer.pos(geom.x, geom.y + geom.h, geom.z);
        buffer.pos(geom.x + geom.w, geom.y + geom.h, geom.z);
        buffer.pos(geom.x + geom.w, geom.y, geom.z);
        buffer.pos(geom.x, geom.y, geom.z);
        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}
