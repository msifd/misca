package msifeed.mc.gui.widgets;

import msifeed.mc.gui.GraphicsHelper;
import msifeed.mc.gui.events.MouseEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class ButtonWidget extends BaseWidget {
    protected String text = "";
    protected Color bg_color = Color.GRAY;
    protected Color text_color = Color.WHITE;
    protected boolean enabled = true;

    public ButtonWidget(String text, int width, int height) {
        this.text = text;
        this.width = width;
        this.height = height;
    }

    @Override
    public void render(Minecraft mc, int mouseX, int mouseY, float tick) {
        if (!visible) return;

        render_background(mc, mouseX, mouseY, tick);

        final FontRenderer fr = mc.fontRenderer;
        int posX = getAbsPosX(), posY = getAbsPosY();
        fr.drawString(text, posX + width / 2 - fr.getStringWidth(text) / 2, posY + height / 2 - fr.FONT_HEIGHT / 2, text_color.getRGB());
    }

    protected void render_background(Minecraft mc, int mouseX, int mouseY, float tick) {
        int posX = getAbsPosX(), posY = getAbsPosY();
        Color bg_color = isPosInBounds(mouseX, mouseY) ? this.bg_color.brighter() : this.bg_color;
        GraphicsHelper.drawColoredBox(bg_color, posX, posY, zLevel, width, height);
    }

    @Override
    public void onMouseEvent(MouseEvent event) {
        if (event.type != MouseEvent.Type.PRESS) return;

        setFocused();
        PositionedSoundRecord sound = PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F);
        Minecraft.getMinecraft().getSoundHandler().playSound(sound);

        onPress(event);
    }

    protected void onPress(MouseEvent event) {
    }
}
