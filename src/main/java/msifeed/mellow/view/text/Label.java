package msifeed.mellow.view.text;

import msifeed.mellow.render.RenderParts;
import msifeed.mellow.view.View;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class Label extends View {
    protected String text = "";
    protected RenderParts.TextPref pref = new RenderParts.TextPref();

    public Label(String text, int color) {
        this.geometry.z = 1;
        setText(text);
        setColor(color);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;

        final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        setSize(fr.getStringWidth(text), fr.FONT_HEIGHT);
    }

    protected int getColor() {
        return pref.color;
    }

    public void setColor(int color) {
        pref.color = color;
    }

    @Override
    public void render() {
        RenderParts.string(text, this.geometry, pref);
    }
}
