package msifeed.mellow.sprite;

import msifeed.mellow.utils.Geom;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class FlatSprite implements ISprite {
    private final SizedTexture texture;
    private final int u, v;
    private final int txw, txh;

    public FlatSprite(SizedTexture tex, int u, int v, int txw, int txh) {
        this.texture = tex;
        this.u = u;
        this.v = v;
        this.txw = txw;
        this.txh = txh;
    }

    @Override
    public void render(Geom geom) {
        GlStateManager.bindTexture(texture.getGlTextureId());
        final double rx = 1. / texture.getWidth();
        final double ry = 1. / texture.getHeight();

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(geom.x, geom.y + geom.h, geom.z)
                .tex(u * rx, (v + txh) * ry).endVertex();
        buffer.pos(geom.x + geom.w, geom.y + geom.h, geom.z)
                .tex((u + txw) * rx, (v + txh) * ry).endVertex();
        buffer.pos(geom.x + geom.w, geom.y, geom.z)
                .tex((u + txw) * rx, v * ry).endVertex();
        buffer.pos(geom.x, geom.y, geom.z)
                .tex(u * rx, v * ry).endVertex();
        tessellator.draw();
    }
}
