package msifeed.mc.gui.widget;

import net.minecraft.client.Minecraft;

import java.awt.*;

public class LabelWidget extends BaseWidget {
    protected String text;
    protected Color color = Color.WHITE;

    public LabelWidget(String text) {
        setText(text);
        setHeight(8);
    }

    @Override
    public void render(Minecraft mc, int mouseX, int mouseY, float tick) {
        mc.fontRenderer.drawString(text, getAbsPosX(), getAbsPosY(), color.getRGB());
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        setWidth(Minecraft.getMinecraft().fontRenderer.getStringWidth(text));
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
