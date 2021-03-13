package msifeed.misca.combat.client;

import msifeed.mellow.render.RenderShapes;
import msifeed.mellow.utils.Geom;
import msifeed.misca.combat.Combat;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.ICombatant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

public class CombatantsBarRender {
    public static float getEntityWidth(EntityLivingBase entity) {
        final float width = (float) (entity.getRenderBoundingBox().maxX - entity.getRenderBoundingBox().minX);
        final float depth = (float) (entity.getRenderBoundingBox().maxZ - entity.getRenderBoundingBox().minZ);
        return Math.max(width, depth);
    }

    public static void renderModel(EntityLivingBase entity, int posX, int posY, int frame, int width) {
        final EntityPlayer player = Minecraft.getMinecraft().player;
        final ICombatant combatant = CombatantProvider.get(entity);

        final float height = (float) (entity.getRenderBoundingBox().maxY - entity.getRenderBoundingBox().minY);
        final float scale = frame / (Math.max(2, height));

        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(posX + width / 2f, posY + frame, 50);
        GlStateManager.scale(-scale, scale, scale);
        GlStateManager.rotate(180, 0, 0, 1);
        RenderHelper.enableStandardItemLighting();

        float ff = entity.prevRenderYawOffset;
        float f3 = entity.prevRotationYawHead;
        float f2 = entity.rotationPitch;

        entity.rotationPitch = 0;

        if (entity == player) {
            final Vec3d pos = combatant.getPosition();
            final double yaw = Math.atan2(pos.x - player.posX, pos.z - player.posZ) / Math.PI / 2 * 360;
            entity.prevRenderYawOffset += (float) yaw;
            entity.prevRotationYawHead += (float) yaw;
        } else {
            final double yaw = Math.atan2(player.posX - entity.posX, player.posZ - entity.posZ) / Math.PI / 2 * 360;
            entity.prevRenderYawOffset += (float) yaw;
            entity.prevRotationYawHead += (float) yaw;
        }

        GlStateManager.translate(0, 0, 0);
        final RenderManager manager = Minecraft.getMinecraft().getRenderManager();
        manager.setPlayerViewY(180);
        manager.setRenderShadow(false);
        manager.renderEntity(entity, 0, 0, 0, 0, 0, false);
        manager.setRenderShadow(true);

        entity.prevRenderYawOffset = ff;
        entity.prevRotationYawHead = f3;
        entity.rotationPitch = f2;

        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public static void renderBars(EntityLivingBase entity, int posX, int posY, int width) {
        final ICombatant combatant = CombatantProvider.get(entity);

//        RenderShapes.rect(new Geom(posX, posY + 2, width, 4), 0xff000000);

        final float healthPercent = entity.getHealth() / entity.getMaxHealth();
        RenderShapes.rect(new Geom(posX, posY + 2, (int) (width * healthPercent), 2), 0xffff1010);

        ActionPointsBarView.render(new Geom(posX, posY + 4, width, 2), entity);
//        final double apPercent = combatant.getActionPoints() / Combat.getRules().maxActionPoints(entity);
//        RenderShapes.rect(new Geom(posX, posY + 4, (int) (width * apPercent), 2), 0xffffff00);
    }
}
