package msifeed.mc.gui.im;

import msifeed.mc.gui.ImStyle;
import msifeed.mc.gui.NimGui;
import msifeed.mc.gui.input.MouseTracker;
import msifeed.mc.gui.render.DrawPrimitives;
import msifeed.mc.gui.render.DrawTexbox;
import msifeed.mc.gui.render.TextureInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ResourceLocation;

public class ImButton {
    public boolean button(Object label, int x, int y, int width, int height) {
        final NimGui nimgui = NimGui.INSTANCE;
        final ImStyle st = nimgui.imStyle;
        final Profiler profiler = Minecraft.getMinecraft().mcProfiler;
        profiler.startSection("ImButton");

        final boolean inRect = MouseTracker.isInRect(x, y, width, height);

        if (label instanceof String) {
            final int textureVMod = inRect
                    ? (MouseTracker.pressed()
                    ? 2
                    : 1)
                    : 0;
            final int vOffset = textureVMod * st.buttonLeftTexture.height;
            DrawTexbox.threeParted(
                    st.buttonLeftTexture, st.buttonMiddleTexture, st.buttonRightTexture,
                    x, y, width, height, vOffset);

            nimgui.imLabel.label((String) label,
                    x + st.buttonPaddingX, y + st.buttonTitleOffset.getY(),
                    width - st.buttonPaddingX * 2, height,
                    st.buttonTitleColor, true, true);
        } else if (label instanceof TextureInfo) {
            final TextureInfo tex = (TextureInfo) label;
            DrawPrimitives.drawTexture(tex, x + st.buttonPaddingX, y, 0, inRect ? tex.height : 0);
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
