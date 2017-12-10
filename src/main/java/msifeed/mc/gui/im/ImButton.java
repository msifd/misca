package msifeed.mc.gui.im;

import msifeed.mc.gui.ImGui;
import msifeed.mc.gui.ImStyle;
import msifeed.mc.gui.input.MouseTracker;
import msifeed.mc.gui.render.DrawPrimitives;
import msifeed.mc.gui.render.TextureInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ResourceLocation;

public class ImButton {
    public boolean button(Object label, int x, int y, int width, int height) {
        final ImGui imgui = ImGui.INSTANCE;
        final ImStyle st = imgui.imStyle;
        final Profiler profiler = Minecraft.getMinecraft().mcProfiler;
        profiler.startSection("ImButton");

        final int spacingWidth = st.buttonSpacingX * 2;
        final boolean inRect = MouseTracker.isInRect(x, y, width, height);

        final int color = inRect
                ? (MouseTracker.pressed()
                ? st.buttonColorPressed
                : st.buttonColorHovered)
                : st.buttonColor;

        if (label instanceof String) {
            DrawPrimitives.drawRect(x, y, x + width, y + height, color);
            imgui.imLabel.label((String) label, x + st.buttonSpacingX, y, width + spacingWidth, height, st.buttonTitleColor, true, true);
        } else if (label instanceof TextureInfo) {
            final TextureInfo tex = (TextureInfo) label;
            DrawPrimitives.drawTexture(tex, x + st.buttonSpacingX, y, 0, inRect ? tex.height : 0);
        }

        profiler.endSection();

        if (inRect && MouseTracker.released()) {
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
