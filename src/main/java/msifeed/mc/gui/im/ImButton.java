package msifeed.mc.gui.im;

import msifeed.mc.gui.font.FontFactory;
import msifeed.mc.gui.render.DrawPrimitives;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class ImButton {
    public boolean button(String label, int x, int y, int width, int height) {
        final int paddingX = 1;
        final int labelHeight = 12;

        DrawPrimitives.drawRect(x, y, x + width, y + height, 0xFF303050);
        FontFactory.fsexFont.renderString(label, x + paddingX, y + (height - labelHeight) / 2, 0xFFFFFFFF, false);

        if (MouseTracker.isInRect(x, y, width, height) && MouseTracker.isClicked()) {
            playClickSound();
            return true;
        }

        return false;
    }

    public void playClickSound() {
        PositionedSoundRecord sound = PositionedSoundRecord.func_147674_a(new ResourceLocation("client.button.press"), 1.0F);
        Minecraft.getMinecraft().getSoundHandler().playSound(sound);
    }
}
