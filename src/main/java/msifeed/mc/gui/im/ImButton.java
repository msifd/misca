package msifeed.mc.gui.im;

import msifeed.mc.gui.render.DrawPrimitives;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ResourceLocation;

public class ImButton {
    public boolean button(String label, int x, int y, int width, int height) {
        Profiler profiler = Minecraft.getMinecraft().mcProfiler;
        profiler.startSection("ImButton");

        final int paddingX = 1
                , paddingWidth = -paddingX - paddingX;
        final boolean inRect = MouseTracker.isInRect(x, y, width, height);

        final int color;
        if (inRect) {
            if (MouseTracker.clickStatus == MouseTracker.ClickStatus.PRESS) color = 0xFF202030;
            else color = 0xFF404060;
        }
        else color = 0xFF303050;

        DrawPrimitives.drawRect(x, y, x + width, y + height, color);
        ImGui.INSTANCE.label(label, x + paddingX, y, width + paddingWidth, height, true);

        profiler.endSection();

        if (inRect && MouseTracker.clickStatus == MouseTracker.ClickStatus.RELEASE) {
            playClickSound();
            return true;
        }
        return false;
    }

    public void playClickSound() {
        PositionedSoundRecord sound = PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F);
        Minecraft.getMinecraft().getSoundHandler().playSound(sound);
    }
}
