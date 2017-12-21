package msifeed.mc.gui.render;

public class DrawTexbox {
    public static void threeParted(TextureInfo left, TextureInfo mid, TextureInfo right, int x, int y, int width, int height, int vOffset) {
        DrawPrimitives.drawTexture(left, x, y, 0.01, vOffset);
        DrawPrimitives.drawScaledTexture(
                mid,
                x + left.width, y, 0.01,
                width - left.width - right.width,
                mid.height, vOffset
        );
        DrawPrimitives.drawTexture(right, x + width - right.width, y, 0.01, vOffset);
    }
}
