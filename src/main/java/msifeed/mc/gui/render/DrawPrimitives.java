package msifeed.mc.gui.render;

import org.lwjgl.opengl.GL11;

public class DrawPrimitives {
    public static void drawRect(int p_73734_0_, int p_73734_1_, int p_73734_2_, int p_73734_3_, int p_73734_4_) {
        int j1;

        if (p_73734_0_ < p_73734_2_) {
            j1 = p_73734_0_;
            p_73734_0_ = p_73734_2_;
            p_73734_2_ = j1;
        }

        if (p_73734_1_ < p_73734_3_) {
            j1 = p_73734_1_;
            p_73734_1_ = p_73734_3_;
            p_73734_3_ = j1;
        }

        float f3 = (float) (p_73734_4_ >> 24 & 255) / 255.0F;
        float f = (float) (p_73734_4_ >> 16 & 255) / 255.0F;
        float f1 = (float) (p_73734_4_ >> 8 & 255) / 255.0F;
        float f2 = (float) (p_73734_4_ & 255) / 255.0F;
        TessellatorEx tessellator = TessellatorEx.INSTANCE;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
//        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(f, f1, f2, f3);
        tessellator.startDrawingQuads();
        tessellator.addVertex((double) p_73734_0_, (double) p_73734_3_, 0.0D);
        tessellator.addVertex((double) p_73734_2_, (double) p_73734_3_, 0.0D);
        tessellator.addVertex((double) p_73734_2_, (double) p_73734_1_, 0.0D);
        tessellator.addVertex((double) p_73734_0_, (double) p_73734_1_, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
