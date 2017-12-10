package msifeed.mc.gui;

import msifeed.mc.gui.render.TextureInfo;
import org.lwjgl.util.Point;

public class ImStyle {
    public static final ImStyle DEFAULT = new ImStyle();

    public int windowHeaderHeight = 10;
    public Point windowPadding = new Point(4, 3);
    public Point windowSpacing = new Point(2, 2);
    public int windowTitleColor = 0xFF6b4c16;
    public Point windowTitleOffset = new Point(4, 1);
    public String windowTexture = "misca:textures/gui/imgui.png";
    public TextureInfo windowTopLeftTexture = new TextureInfo(windowTexture, 0, 0, 9, 10);
    public TextureInfo windowTopMiddleTexture = new TextureInfo(windowTexture, 10, 0, 4, 10);
    public TextureInfo windowTopRightTexture = new TextureInfo(windowTexture, 15, 0, 9, 10);
    public TextureInfo windowMiddleLeftTexture = new TextureInfo(windowTexture, 0, 11, 9, 4);
    public TextureInfo windowMiddleMiddleTexture = new TextureInfo(windowTexture, 10, 11, 4, 4);
    public TextureInfo windowMiddleRightTexture = new TextureInfo(windowTexture, 15, 11, 9, 4);
    public TextureInfo windowBottomLeftTexture = new TextureInfo(windowTexture, 0, 16, 9, 9);
    public TextureInfo windowBottomMiddleTexture = new TextureInfo(windowTexture, 10, 16, 4, 9);
    public TextureInfo windowBottomRightTexture = new TextureInfo(windowTexture, 15, 16, 9, 9);
    public TextureInfo windowCloseBtnTexture = new TextureInfo(windowTexture, 24, 0, 6, 6);
    public Point windowCloseBtnOffset = new Point(-4, 3);

    public int buttonSpacingX = 1;
    public int buttonSpacingY = 1;
    public int buttonTitleColor = 0xFFedb47d;
    public Point buttonTitleOffset = new Point(0, 0);
    public String buttonTexture = "misca:textures/gui/imgui.png";
    public TextureInfo buttonLeftTexture = new TextureInfo(buttonTexture, 50, 0, 4, 12);
    public TextureInfo buttonMiddleTexture = new TextureInfo(buttonTexture, 55, 0, 2, 12);
    public TextureInfo buttonRightTexture = new TextureInfo(buttonTexture, 58, 0, 4, 12);

    public int textLabelColor = 0xFFf2c399;
    public Point textLabelOffset = new Point(2, 0);
    public int textCursorHeight = 6;
    public String textTexture = "misca:textures/gui/imgui.png";
    public TextureInfo textLeftTexture = new TextureInfo(textTexture, 80, 0, 4, 8);
    public TextureInfo textMiddleTexture = new TextureInfo(textTexture, 85, 0, 1, 8);
    public TextureInfo textRightTexture = new TextureInfo(textTexture, 87, 0, 4, 8);

    public int labelColor = 0xFFf2c399;
}
