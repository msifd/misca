package msifeed.mellow.view.button;

import msifeed.mellow.render.RenderParts;
import msifeed.mellow.render.RenderShapes;
import msifeed.mellow.utils.Geom;
import msifeed.mellow.view.InputHandler;
import msifeed.mellow.view.View;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class ButtonLabel extends View implements InputHandler.MouseClick {
    protected String text = "";
    protected RenderParts.TextPref pref = new RenderParts.TextPref();
    protected Geom textOffset = new Geom(2, 2, 0, 0);

    protected int colorNormal = 0xffffffff;
    protected int colorHover = 0xff707070;

    protected Runnable callback = () -> {};

    public ButtonLabel(String text) {
        setText(text);

        final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        setSize(fr.getStringWidth(text) + textOffset.x + 1, fr.FONT_HEIGHT + textOffset.y);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

    @Override
    public void render(Geom geom) {
        final int color = isHovered() ? colorHover : colorNormal;
        RenderShapes.rect(geom, 0xbb000000);

        final Geom textGeom = geom.add(textOffset);
        RenderParts.string(text, textGeom, color, pref);
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int button) {
        callback.run();
    }
}
