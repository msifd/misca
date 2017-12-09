package msifeed.mc.imgui.parts;

import msifeed.mc.imgui.ImGui;
import msifeed.mc.imgui.input.MouseTracker;
import msifeed.mc.imgui.render.DrawPrimitives;
import msifeed.mc.imgui.render.TextureInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ResourceLocation;

public class ImButton {
    public boolean button(Object label, int x, int y, int width, int height) {
        final ImGui imgui = ImGui.INSTANCE;
        final ImStyle st = imgui.imStyle;
        final Profiler profiler = Minecraft.getMinecraft().mcProfiler;

        final int spacingWidth = st.buttonSpacingX * 2;
        final boolean inRect = MouseTracker.isInRect(x, y, width, height);

        final int color = inRect
                ? (MouseTracker.pressed()
                ? st.buttonColorPressed
                : st.buttonColorHovered)
                : st.buttonColor;

        profiler.startSection("ImButton");
        DrawPrimitives.drawRect(x, y, x + width, y + height, color);
        profiler.endSection();

        if (label instanceof String) {
            imgui.imLabel.label((String) label, x + st.buttonSpacingX, y, width + spacingWidth, height, st.buttonTitleColor, true, true);
        } else if (label instanceof TextureInfo) {
            DrawPrimitives.drawTexture((TextureInfo) label, x + st.buttonSpacingX, y, 0);
        }

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
