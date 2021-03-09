package msifeed.mellow.view.button;

import msifeed.mellow.render.RenderParts;
import msifeed.mellow.utils.Geom;
import msifeed.mellow.view.InputHandler;
import msifeed.mellow.view.View;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class ButtonLabel extends View implements InputHandler.MouseClick {
    protected String text = "";
    protected RenderParts.TextPref pref = new RenderParts.TextPref();

    protected int colorNormal = 0xffffffff;
    protected int colorHover = 0xff707070;

    protected Runnable callback = () -> {};

    public ButtonLabel(String text) {
        setText(text);

        final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        setSize(fr.getStringWidth(text), 10);
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
        RenderParts.string(text, geom, color, pref);
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int button) {
        callback.run();
    }
}
