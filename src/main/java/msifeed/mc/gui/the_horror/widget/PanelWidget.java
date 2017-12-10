package msifeed.mc.gui.the_horror.widget;

import msifeed.mc.gui.the_horror.GraphicsHelper;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class PanelWidget extends BaseWidget {
    private Color color = Color.GRAY;

    public PanelWidget(int width, int height) {
        super(width, height);
    }

    @Override
    public void render(Minecraft mc, int mouseX, int mouseY, float tick) {
        GraphicsHelper.drawColoredBox(color, getAbsPosX(), getAbsPosY(), zLevel, width, height);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
