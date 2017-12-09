package msifeed.mc.misca.crabs.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.mc.misca.crabs.battle.BattleManager;
import msifeed.mc.misca.crabs.battle.FighterContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import org.lwjgl.opengl.GL11;

public enum BattleMarkRender {
    INSTANCE;

    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Post event) {
        EntityPlayer self = Minecraft.getMinecraft().thePlayer;
        if (event.entity == self) return;

        final FighterContext self_ctx = BattleManager.INSTANCE.getContext(self.getUniqueID());
        final FighterContext entity_ctx = BattleManager.INSTANCE.getContext(event.entity);
        if (self_ctx == null || entity_ctx == null) return;

        final boolean isUnderMyControl = self_ctx.control == entity_ctx.uuid;
        final int color = isUnderMyControl ? 0x808020 : 0x802020;

        // Рендер плашки с режимом боя над энтити
        RenderManager renderManager = RenderManager.instance;
        float f = 1.6F;
        float f1 = 0.016666668F * f;
        GL11.glPushMatrix();
        GL11.glTranslatef((float) event.x, (float) event.y + event.entity.height + 1F, (float) event.z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-f1, -f1, f1);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        final double icon_half = 5;

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_I(color, 200);
        tessellator.addVertex(-icon_half, -icon_half, 0.0D);
        tessellator.addVertex(-icon_half, icon_half, 0.0D);
        tessellator.addVertex(icon_half, icon_half, 0.0D);
        tessellator.addVertex(icon_half, -icon_half, 0.0D);
        tessellator.draw();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }
}
