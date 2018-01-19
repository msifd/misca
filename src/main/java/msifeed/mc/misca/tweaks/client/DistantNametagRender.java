package msifeed.mc.misca.tweaks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.client.event.RenderLivingEvent;
import org.lwjgl.opengl.GL11;

public class DistantNametagRender {
    public static void render(RenderLivingEvent event, float distance) {
        final String title = String.format("%s(%dm)", event.entity.getCommandSenderName(), Math.round(distance));

        final FontRenderer fontrenderer = Minecraft.getMinecraft().fontRenderer;
        final RenderManager renderManager = RenderManager.instance;

        final float f = 1.6F;
        final float f1 = 0.016666668F * f;
        GL11.glPushMatrix();
        GL11.glTranslatef((float) event.x, (float) event.y + event.entity.height + 0.5F, (float) event.z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-0.5f, -0.5f, 0.5f);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glTranslatef(0.0F, -0.25F / f1, 0.0F);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        final int bgSize = fontrenderer.getStringWidth(title) / 2;

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.8F);
        tessellator.addVertex(-bgSize - 1, -1, 0.0D);
        tessellator.addVertex(-bgSize - 1, 8, 0.0D);
        tessellator.addVertex(bgSize + 1, 8, 0.0D);
        tessellator.addVertex(bgSize + 1, -1, 0.0D);
        tessellator.draw();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        fontrenderer.drawString(title, -bgSize, 0, 553648127);

        GL11.glEnable(GL11.GL_FOG);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }
}
