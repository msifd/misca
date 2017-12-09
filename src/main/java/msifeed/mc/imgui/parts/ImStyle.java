package msifeed.mc.imgui.parts;

import msifeed.mc.imgui.render.TextureInfo;

public class ImStyle {
    public int windowHeaderHeight = 10;
    public int windowSpacingX = 2;
    public int windowSpacingY = 2;
    public int windowBgColor = 0xFF502020;
    public int windowHeaderColor = 0xFF451515;
    public int windowHeaderColorHovered = 0xFF451515;
    public int windowHeaderColorPressed = 0xFF401010;
    public int windowTitleColor = 0xDDFFFFFF;
    public String windowTexture = "misca:textures/gui/imgui_window.png";
    public TextureInfo windowCrossBtnTexture = new TextureInfo(windowTexture, 0, 0, 7, 7);

    public int buttonSpacingX = 1;
    public int buttonSpacingY = 1;
    public int buttonColor = 0xFF451515;
    public int buttonColorHovered = 0xFF481818;
    public int buttonColorPressed = 0xFF401010;
    public int buttonTitleColor = 0xFFFFFFFF;
}
