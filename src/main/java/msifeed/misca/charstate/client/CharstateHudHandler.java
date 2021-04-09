package msifeed.misca.charstate.client;

import msifeed.mellow.render.RenderUtils;
import msifeed.misca.Misca;
import msifeed.misca.charstate.handler.CorruptionHandler;
import msifeed.misca.charstate.handler.IntegrityHandler;
import msifeed.misca.charstate.handler.SanityHandler;
import msifeed.misca.charstate.handler.StaminaHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CharstateHudHandler {
    final ResourceLocation STATS = new ResourceLocation(Misca.MODID, "textures/gui/stats.png");

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent.Post event) {
        RenderGameOverlayEvent.ElementType type = event.getType();
        Minecraft mc = Minecraft.getMinecraft();

        if (type == RenderGameOverlayEvent.ElementType.ALL) {
            final ScaledResolution resolution = RenderUtils.getScaledResolution();
            final EntityPlayerSP player = mc.player;

            final IAttributeInstance integrityInstance = player.getEntityAttribute(IntegrityHandler.INTEGRITY);
            final double integrity = integrityInstance.getAttributeValue();
            final String integrityText = String.format("%.1f", integrity);
            final double integrityLevel = Math.floor(integrity / 25);

            final IAttributeInstance staminaInstance = player.getEntityAttribute(StaminaHandler.STAMINA);
            final double stamina = staminaInstance.getAttributeValue();
            final String staminaText = Math.round(stamina) + "%";
            final double staminaLevel = Math.floor(stamina / 25);

            final IAttributeInstance corruptionInstance = player.getEntityAttribute(CorruptionHandler.CORRUPTION);
            final double corruption = corruptionInstance.getAttributeValue();
            final String corruptionText = String.format("%.1f", corruption);
            final double corruptionLevel = Math.floor((100 - corruption) / 25);

            final IAttributeInstance sanityInstance = player.getEntityAttribute(SanityHandler.SANITY);
            final double sanity = sanityInstance.getAttributeValue();
            final String sanityText = String.format("%.1f", sanity);
            final double sanityLevel = Math.floor(sanity / 25);

            final int width = resolution.getScaledWidth();
            final int height = resolution.getScaledHeight();
            final int xLeft = width / 2 + 107;
            final int xRight = width / 2 + 167;
            final int y = height - 14;
            final GuiIngame gui = mc.ingameGUI;

            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.translate(0, 0, -999);
            gui.drawString(mc.fontRenderer, integrityText, xLeft, y, 16777215);
            gui.drawString(mc.fontRenderer, staminaText, xRight - mc.fontRenderer.getStringWidth(staminaText), y, 16777215);
            gui.drawString(mc.fontRenderer, corruptionText, xLeft, y - 13, 16777215);
            gui.drawString(mc.fontRenderer, sanityText, xRight - mc.fontRenderer.getStringWidth(sanityText), y - 13, 16777215);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.scale(0.25, 0.25, 1);
            Minecraft.getMinecraft().getTextureManager().bindTexture(STATS);
            gui.drawTexturedModalRect((xLeft - 14) * 4, (y - 2) * 4, 0, 48 * (3 - (int)Math.min(integrityLevel, 3)), 48, 48);
            gui.drawTexturedModalRect((xRight + 2) * 4, (y - 2) * 4, 48, 48 * (3 - (int)Math.min(staminaLevel, 3)), 48, 48);
            gui.drawTexturedModalRect((xLeft - 14) * 4, (y - 15) * 4, 96, 48 * (3 - (int)Math.min(corruptionLevel, 3)), 48, 48);
            gui.drawTexturedModalRect((xRight + 2) * 4, (y - 15) * 4, 144, 48 * (3 - (int)Math.min(sanityLevel, 3)), 48, 48);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }
}
