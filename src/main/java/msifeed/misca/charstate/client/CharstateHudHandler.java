package msifeed.misca.charstate.client;

import msifeed.mellow.render.RenderUtils;
import msifeed.misca.Misca;
import msifeed.misca.charstate.handler.CorruptionHandler;
import msifeed.misca.charstate.handler.IntegrityHandler;
import msifeed.misca.charstate.handler.SanityHandler;
import msifeed.misca.charstate.handler.StaminaHandler;
import msifeed.misca.chatex.Chatex;
import msifeed.misca.chatex.client.gui.ChatexHud;
import msifeed.misca.chatex.client.gui.ChatexScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CharstateHudHandler {
    ResourceLocation STATS = new ResourceLocation(Misca.MODID, "textures/gui/stats.png");

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent.Post event) {
        RenderGameOverlayEvent.ElementType type = event.getType();
        Minecraft mc = Minecraft.getMinecraft();

        if (type == RenderGameOverlayEvent.ElementType.ALL) {
            final ScaledResolution resolution = RenderUtils.getScaledResolution();
            EntityPlayerSP player = mc.player;

            final IAttributeInstance integrityInstance = player.getEntityAttribute(IntegrityHandler.INTEGRITY);
            double integrity = integrityInstance.getAttributeValue();
            String integrityText = String.format("%.1f", integrity);

            final IAttributeInstance staminaInstance = player.getEntityAttribute(StaminaHandler.STAMINA);
            double stamina = staminaInstance.getAttributeValue() * 100;
            String staminaText = Math.round(stamina) + "%";

            final IAttributeInstance corruptionInstance = player.getEntityAttribute(CorruptionHandler.CORRUPTION);
            double corruption = corruptionInstance.getAttributeValue();
            String corruptionText = String.format("%.1f", corruption);

            final IAttributeInstance sanityInstance = player.getEntityAttribute(SanityHandler.SANITY);
            double sanity = sanityInstance.getAttributeValue();
            String sanityText = String.format("%.1f", sanity);

            int width = resolution.getScaledWidth();
            int height = resolution.getScaledHeight();
            int xLeft = width / 2 - 81;
            int xRight = width / 2 - 21;
            int y = height - GuiIngameForge.left_height + 8;

            GuiIngameForge.left_height += 20;

            GuiIngame gui = mc.ingameGUI;

            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.translate(0, 0, -999);
            gui.drawString(mc.fontRenderer, integrityText, xLeft, y, 16777215);
            gui.drawString(mc.fontRenderer, staminaText, xRight - mc.fontRenderer.getStringWidth(staminaText), y, 16777215);
            gui.drawString(mc.fontRenderer, corruptionText, xLeft, y - 10, 16777215);
            gui.drawString(mc.fontRenderer, sanityText, xRight - mc.fontRenderer.getStringWidth(sanityText), y - 10, 16777215);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.scale(0.2, 0.2, 1);
            Minecraft.getMinecraft().getTextureManager().bindTexture(STATS);
            gui.drawTexturedModalRect((xLeft - 12) * 5, (y - 1) * 5, 0, 0, 48, 48);
            gui.drawTexturedModalRect((xRight + 2) * 5, (y - 1) * 5, 48, 0, 48, 48);
            gui.drawTexturedModalRect((xLeft - 12) * 5, (y - 11) * 5, 96, 0, 48, 48);
            gui.drawTexturedModalRect((xRight + 2) * 5, (y - 11) * 5, 144, 0, 48, 48);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }
}
