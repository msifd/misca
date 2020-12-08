package msifeed.mellow.sprite;

import msifeed.mellow.utils.Geom;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class Slice9Sprite implements ISprite {
    private final ITextureObject texture;
    private final Slice[] slices;
    private final int fixedWidth, fixedHeight;
    private final int txw, txh;

    public Slice9Sprite(SizedTexture tex,
                        int u, int v,
                        int w1, int h1,
                        int w2, int h2,
                        int w3, int h3) {
//        this(tex, tex.getWidth(), tex.getHeight(), u, v, w1, h1, w2, h2, w3, h3);
        this(tex, 256, 256, u, v, w1, h1, w2, h2, w3, h3);
    }

    public Slice9Sprite(ITextureObject tex, int txw, int txh,
                        int u, int v,
                        int w1, int h1,
                        int w2, int h2,
                        int w3, int h3) {
        this.texture = tex;
        this.slices = new Slice[]{
                new Slice(u, v, w1, h1),
                new Slice(u + w1, v, w2, h1),
                new Slice(u + w1 + w2, v, w3, h1),
                new Slice(u, v + h1, w1, h2),
                new Slice(u + w1, v + h1, w2, h2),
                new Slice(u + w1 + w2, v + h1, w3, h2),
                new Slice(u, v + h1 + h2, w1, h3),
                new Slice(u + w1, v + h1 + h2, w2, h3),
                new Slice(u + w1 + w2, v + h1 + h2, w3, h3),
        };
        this.fixedWidth = w1 + w3;
        this.fixedHeight = h1 + h3;
        this.txw = txw;
        this.txh = txh;
    }

    @Override
    public void render(Geom geom) {
        GlStateManager.bindTexture(texture.getGlTextureId());
        final double rx = 1. / txw;
        final double ry = 1. / txh;

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();
        // TODO: optimize quads
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        final double midWidth = Math.max(geom.w - fixedWidth, 0);
        final double midHeight = Math.max(geom.w - fixedHeight, 0);

        double x = geom.x;
        double y = geom.y;
        for (int i = 0; i < 9; i++) {
            final int xth = i % 3;
            final int yth = i / 3;

            final Slice s = slices[i];
            final double w = xth == 1 ? midWidth : s.w;
            final double h = yth == 1 ? midHeight : s.h;

            buffer.pos(x, y + h, geom.z)
                    .tex(s.u * rx, (s.v + s.h) * ry).endVertex();
            buffer.pos(x + w, y + h, geom.z)
                    .tex((s.u + s.w) * rx, (s.v + s.h) * ry).endVertex();
            buffer.pos(x + w, y, geom.z)
                    .tex((s.u + s.w) * rx, s.v * ry).endVertex();
            buffer.pos(x, y, geom.z)
                    .tex(s.u * rx, s.v * ry).endVertex();

            if (xth == 2) {
                x = geom.x;
                y += h;
            } else {
                x += w;
            }
        }

        tessellator.draw();
    }

    private static class Slice {
        final int u, v;
        final int w, h;

        Slice(int u, int v, int w, int h) {
            this.u = u;
            this.v = v;
            this.w = w;
            this.h = h;
        }
    }
}
