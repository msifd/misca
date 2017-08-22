package ru.ariadna.misca.twowayradio;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;
import ru.ariadna.misca.MiscaUtils;

public class WalkieTalkieAdjustmentHud extends Gui {
    private static final ResourceLocation texture = new ResourceLocation("misca", "textures/gui/walkie-talkie_hud.png");
    private static final int[] SCALE_SIZE = {100, 10};
    private static final int SCALE_WIDTH_HALF = SCALE_SIZE[0] / 2;

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (!ItemWalkieTalkie.adjusting || event.type != RenderGameOverlayEvent.ElementType.CROSSHAIRS) return;

        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fr = mc.fontRenderer;
        int width = event.resolution.getScaledWidth();
        int height = event.resolution.getScaledHeight();
        float frequency = ItemWalkieTalkie.adjusted_fq;

        int scale_x = width / 2 - SCALE_WIDTH_HALF;
        int scale_y = height / 2 + SCALE_SIZE[1];
        float mark_scale = (frequency - ItemWalkieTalkie.FQ_RANGE_LOW) / (ItemWalkieTalkie.FQ_RANGE_HIGH - ItemWalkieTalkie.FQ_RANGE_LOW);
        int mark_x = scale_x + (int) ((SCALE_SIZE[0] - 2) * mark_scale);
        int mark_y = scale_y - 1;

        mc.getTextureManager().bindTexture(texture);
        GL11.glEnable(GL11.GL_BLEND);

        OpenGlHelper.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR, 1, 0);
        drawTexturedModalRect(scale_x, scale_y, 0, 0, SCALE_SIZE[0], SCALE_SIZE[1]);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        drawTexturedModalRect(mark_x, mark_y, 0, 10, 2, 10);

        GL11.glDisable(GL11.GL_BLEND);

        String fq_str = String.format("%.2f MHz", frequency);
        fr.drawStringWithShadow(fq_str, (width - fr.getStringWidth(fq_str)) / 2, scale_y + 10 + fr.FONT_HEIGHT, 0xFFFFFF);
    }
}
