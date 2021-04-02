package msifeed.misca.combat.client;

import msifeed.mellow.render.RenderShapes;
import msifeed.mellow.utils.Geom;
import msifeed.misca.combat.Combat;
import msifeed.misca.combat.battle.Battle;
import msifeed.misca.combat.battle.BattleStateClient;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.ICombatant;
import msifeed.misca.combat.rules.Rules;
import msifeed.misca.combat.rules.WeaponInfo;
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

        if (entity.getUniqueID().equals(player.getUniqueID())) {
            final Vec3d pos = CombatantProvider.get(entity).getPosition();
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
        final ICombatant com = CombatantProvider.get(entity);
        final Battle battle = BattleStateClient.STATE;

        final float health = Math.max(0, entity.getHealth() - com.getNeutralDamage());
        final float healthPercent = health / entity.getMaxHealth();
        RenderShapes.rect(new Geom(posX, posY + 2, (int) (width * healthPercent), 2), 0xffff1010);

        final boolean leader = battle.isLeader(entity.getUniqueID());
        final float delayPercent = (float) battle.finishMoveDelayLeft() / Combat.getRules().finishTurnDelayMillis;

        if (leader && delayPercent > 0) {
            RenderShapes.rect(new Geom(posX, posY + 4, (int) (width * delayPercent), 2), 0xff10ff10);
        } else {
            renderActionPointsBar(new Geom(posX, posY + 4, width, 2), entity);
        }
    }

    private static void renderActionPointsBar(Geom geom, EntityLivingBase entity) {
        final EntityPlayer player = Minecraft.getMinecraft().player;
        final boolean isPlayersPuppet = CombatantProvider.get(player).getPuppet() == entity.getEntityId();

        final ICombatant com = CombatantProvider.get(entity);
        final EntityLivingBase weaponHolder = isPlayersPuppet ? player : entity;
        final WeaponInfo weapon = Combat.getWeapons().get(weaponHolder, weaponHolder.getHeldItemMainhand());

        final Rules rules = Combat.getRules();
        final double fullAp = com.getActionPoints();
        final double moveAp = rules.movementActionPoints(entity, com.getPosition(), entity.getPositionVector());
        final double attackAp = rules.attackActionPoints(entity, weapon);
        final double ap = Math.max(com.getActionPoints() - moveAp, 0);

        final int bgColor = ap > 0 ? 0xfff0f000 : 0xffff0000;
        final double pxPerAp = geom.w / fullAp;

        final Geom barGeom = geom.clone();

//        // Single attack
//        barGeom.y += barGeom.h;
//        barGeom.w = (int) ((actionAp + com.getActionPointsOverhead()) * pxPerAp);
//        RenderShapes.rect(barGeom, bgColor);
//        barGeom.y -= barGeom.h;

        barGeom.w = (int) ((ap) * pxPerAp);
        RenderShapes.rect(barGeom, bgColor);

        if (attackAp <= 0) {
            return;
        }

        // Available attacks marks
        barGeom.w = 1;
        double consumedAp = 0;
        double nextAp = attackAp + com.getActionPointsOverhead();
        while (ap >= consumedAp + nextAp) {
            barGeom.x = (int) (geom.x + (consumedAp + nextAp) * pxPerAp);
            RenderShapes.rect(barGeom, 0xff505050);

            consumedAp += nextAp;
            nextAp += nextAp * weapon.overhead;
        }
    }
}
