package msifeed.misca.combat.client;

import msifeed.mellow.render.RenderShapes;
import msifeed.mellow.sprite.FlatSprite;
import msifeed.mellow.utils.Geom;
import msifeed.mellow.view.View;
import msifeed.misca.combat.Combat;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.ICombatant;
import msifeed.misca.combat.rules.Rules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class CombatantsBarRender {
    public static float getEntityWidth(EntityLivingBase entity) {
        final float width = (float) (entity.getRenderBoundingBox().maxX - entity.getRenderBoundingBox().minX);
        final float depth = (float) (entity.getRenderBoundingBox().maxZ - entity.getRenderBoundingBox().minZ);
        return Math.max(width, depth);
    }

    public static void renderModel(EntityLivingBase entity, int posX, int posY, int frame, int width) {
        final EntityPlayer player = Minecraft.getMinecraft().player;
        final ICombatant combatant = CombatantProvider.get(entity);

        // rotationYaw = Math.atan(mouseX / 40.0F) * 40.0F
//        final float yaw = (float) Math.tan(player.rotationYaw / 40) * 40;
//        GuiInventory.drawEntityOnScreen(geom.x, geom.y + 100, 24, yaw, 0, entity);


        final float height = (float) (entity.getRenderBoundingBox().maxY - entity.getRenderBoundingBox().minY);
//        final float width = getEntityWidth(entity);

//        final int frame = 32;
//        final float renderOffsetX = width * frame;
//        final int renderOffsetX = (int) (Math.max(width, depth) * 20);
//        RenderShapes.rect(new Geom(geom.x, 60, renderOffsetX, 2), 0xffffffff);


//        final float posX = geom.x + renderOffsetX / 2;
//        final float posY = frame;
        final float scale = frame / (Math.max(2, height));

        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(posX + width / 2f, posY + frame, 50);
        GlStateManager.scale((-scale), scale, scale);
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

//            entity.prevRenderYawOffset = entity.prevRenderYawOffset % 90 + 45;
//            entity.prevRenderYawOffset = (float) (Math.atan(entity.prevRenderYawOffset / 40) * 20);
//            entity.prevRotationYawHead = entity.prevRenderYawOffset;
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

//        final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
//        fr.drawString(String.valueOf(Math.tan(entity.rotationYawHead)), (int) (posX * 4) - 700, 200, 0xffffffff);
//        fr.drawString(String.valueOf(f1), (int) (posX * 2.5) - 300, 210, 0xffffffff);
    }

    public static void renderBars(EntityLivingBase entity, int posX, int posY, int width) {
        final ICombatant combatant = CombatantProvider.get(entity);

        final float healthPercent = entity.getHealth() / entity.getMaxHealth();
        RenderShapes.rect(new Geom(posX, posY + 2, (int) (width * healthPercent), 2), 0xffff0000);

        final double apPercent = combatant.getActionPoints() / Combat.getRules().maxActionPoints(entity);
        RenderShapes.rect(new Geom(posX, posY + 4, (int) (width * apPercent), 2), 0xffffff00);
    }
}
