package msifeed.mc.gui;

import msifeed.mc.gui.render.TextureInfo;
import org.lwjgl.util.Point;

public class ImStyle {
    public static final ImStyle DEFAULT = new ImStyle();

    public int windowHeaderHeight = 10;
    public int windowSpacingX = 3;
    public int windowSpacingY = 3;
    public int windowTitleColor = 0xFF6b4c16;
    public Point windowTitleOffset = new Point(3, 1);
    public String windowTexture = "misca:textures/gui/imgui_window.png";
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
    public int buttonColor = 0xFF451515;
    public int buttonColorHovered = 0xFF481818;
    public int buttonColorPressed = 0xFF401010;
    public int buttonTitleColor = 0xFFFFFFFF;

    public int textSpacingX = 1;
    public int textSpacingY = 1;
    public int textBackgroundColor = 0xFF401010;
    public int textTextColor = 0xFFFFFFFF;
}
