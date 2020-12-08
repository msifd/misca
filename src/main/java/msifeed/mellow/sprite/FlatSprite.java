package msifeed.mellow.sprite;

import msifeed.mellow.utils.Geom;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class FlatSprite implements ISprite {
    public final ITextureObject texture;
    public final double u, v;
    public final double w, h;
    private final double rx, ry;

    public FlatSprite(ITextureObject tex, double u, double v, double w, double h) {
        this(tex, 256, 256, u, v, w, h);
    }

    public FlatSprite(ITextureObject tex, double txw, double txh, double u, double v, double w, double h) {
        this.texture = tex;
        this.u = u;
        this.v = v;
        this.w = w;
        this.h = h;
        this.rx = 1 / txw;
        this.ry = 1 / txh;
    }

    @Override
    public void render(Geom geom) {
        GlStateManager.bindTexture(texture.getGlTextureId());

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(geom.x, geom.y + geom.h, geom.z)
                .tex(u * rx, (v + h) * ry).endVertex();
        buffer.pos(geom.x + geom.w, geom.y + geom.h, geom.z)
                .tex((u + w) * rx, (v + h) * ry).endVertex();
        buffer.pos(geom.x + geom.w, geom.y, geom.z)
                .tex((u + w) * rx, v * ry).endVertex();
        buffer.pos(geom.x, geom.y, geom.z)
                .tex(u * rx, v * ry).endVertex();
        tessellator.draw();
    }
}
