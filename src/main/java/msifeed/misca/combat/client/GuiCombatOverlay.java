package msifeed.misca.combat.client;

import msifeed.mellow.render.RenderUtils;
import msifeed.misca.client.MiscaConfig;
import msifeed.misca.combat.Combat;
import msifeed.misca.combat.CombatFlow;
import msifeed.misca.combat.battle.Battle;
import msifeed.misca.combat.battle.BattleStateClient;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.ICombatant;
import msifeed.misca.combat.rules.Rules;
import msifeed.misca.combat.rules.WeaponInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class GuiCombatOverlay {
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        final EntityPlayer player = Minecraft.getMinecraft().player;
        if (!CombatantProvider.get(player).isInBattle()) return;

        drawHud();

        if (MiscaConfig.combatDebug) {
            drawDebugInfo();
        }
    }

    private void drawHud() {
        final Battle state = BattleStateClient.STATE;

        final ScaledResolution resolution = RenderUtils.getScaledResolution();
        final int frame = 32;
        final int gap = 2;
        final int barFullWidth = (int) state.getCombatants()
                .mapToDouble(CombatantsBarRender::getEntityWidth)
                .map(val -> val * frame + gap)
                .sum();


        final int[] posX = {(resolution.getScaledWidth() - barFullWidth) / 2};

        state.getCombatants().forEach(entity -> {
            final int frameWidth = (int) (CombatantsBarRender.getEntityWidth(entity) * frame);
            CombatantsBarRender.renderModel(entity, posX[0], 0, frame, frameWidth);
            CombatantsBarRender.renderBars(entity, posX[0], frame, frameWidth);

            posX[0] += frameWidth + gap;
        });
    }

    private static void drawDebugInfo() {
        final Rules rules = Combat.getRules();
        final Battle state = BattleStateClient.STATE;
        final EntityLivingBase leader = state.getLeader();

        final EntityLivingBase actor = CombatFlow.getCombatActor(leader);
        if (actor == null) return;
        final ICombatant com = CombatantProvider.get(actor);
        final WeaponInfo weapon = Combat.getWeapons().get(leader.getHeldItemMainhand());


        final Vec3d pos = com.getPosition();
        final String joinedMembers = state.getMembers().entrySet().stream()
                .map(GuiCombatOverlay::getMemberEntryName)
                .collect(Collectors.joining(", "));
        final String joinedQueue = state.getQueue().stream()
                .map(GuiCombatOverlay::getMemberName)
                .collect(Collectors.joining(", "));
        final String leaderName = getNameOrUuid(state.getLeaderUuid(), state.getLeader());
        final String posStr = String.format("x: %.2f, y: %.2f, z: %.2f", pos.x, pos.y, pos.z);
        final double moveAp = rules.movementActionPoints(actor, com.getPosition(), actor.getPositionVector());
        final double overheadAp = com.getActionPointsOverhead();
        final double attackAp = rules.attackActionPoints(actor, weapon) + overheadAp;
        final double usageAp = rules.usageActionPoints(weapon) + overheadAp;

        final String[] lines = {
                "actor: " + actor.getName(),
                "health: " + String.format("%.2f/%.2f", actor.getHealth(), actor.getMaxHealth()),
                "neutral dmg: " + com.getNeutralDamage(),
                "----",
                "AP: " + String.format("%.2f", com.getActionPoints()),
                "MOVEMENT: " + formatActionPoints(moveAp, com.getActionPoints()),
                "OVERHEAD: " + String.format("%.2f", overheadAp),
                "ATTACK: " + formatActionPoints(attackAp, com.getActionPoints()),
                "USAGE : " + formatActionPoints(usageAp, com.getActionPoints()),
        };

        final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        for (int i = 0; i < lines.length; i++)
            fr.drawString(lines[i], 10, 10 + fr.FONT_HEIGHT * i, 0xffffffff);
    }

    private static String formatActionPoints(double ap, double total) {
        if (total > ap)
            return String.format("%.2f", ap);
        else
            return String.format("\u00a74%.2f\u00a7r", ap);
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderWorldLastEvent event) {
        final EntityPlayer player = Minecraft.getMinecraft().player;
        final ICombatant com = CombatantProvider.get(player);
        if (!com.isInBattle()) return;

        renderTextAt("pos", com.getPosition().x, com.getPosition().y + 1.2, com.getPosition().z, true);
    }

//    @SubscribeEvent
//    public void onRenderEntity(RenderLivingEvent.Post<EntityLivingBase> event) {
//        if (!MiscaConfig.combatDebug) return;
//
//        final EntityPlayer self = Minecraft.getMinecraft().player;
//        final EntityLivingBase entity = event.getEntity();
//        if (self == entity) return;
//
//        if (!CombatantProvider.get(self).isInBattle()) return;
//
//        final ICombatant com = CombatantProvider.get(entity);
//        if (!com.isInBattle()) return;
//
//        {
//            final double cover = Combat.getRules().coverBlocks(self.world, self.getPositionVector(), entity.getPositionVector());
//            final String coverStr = String.format("your cov: %.1f", cover);
//            renderTextAt(coverStr, event.getX(), event.getY() + event.getEntity().getEyeHeight() + 1.2, event.getZ(), false);
//        }
//        {
//            final double cover = Combat.getRules().coverBlocks(self.world, entity.getPositionVector(), self.getPositionVector());
//            final String coverStr = String.format("its cov: %.1f", cover);
//            renderTextAt(coverStr, event.getX(), event.getY() + event.getEntity().getEyeHeight() + 1.0, event.getZ(), false);
//        }
//
//        final Rules rules = Combat.getRules();
//        final double atkAp = rules.attackActionPoints(entity) + com.getActionPointsOverhead();
//        final double movAp = rules.movementActionPoints(com.getPosition(), entity.getPositionVector());
//        final String apStr = String.format("ap: %.1f, atk: %.1f, mov: %.1f", com.getActionPoints(), atkAp, movAp);
//        renderTextAt(apStr, event.getX(), event.getY() + event.getEntity().getEyeHeight() + 0.8, event.getZ(), false);
//
//        final String hpStr = String.format("hp: %.1f/%.1f, neu dmg: %.1f", entity.getHealth(), entity.getMaxHealth(), com.getNeutralDamage());
//        renderTextAt(hpStr, event.getX(), event.getY() + event.getEntity().getEyeHeight() + 0.6, event.getZ(), false);
//    }

    private static void renderTextAt(String str, double posX, double posY, double posZ, boolean absPos) {
        final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        final RenderManager rm = Minecraft.getMinecraft().getRenderManager();
        float viewerYaw = rm.playerViewY;
        float viewerPitch = rm.playerViewX;

        GlStateManager.pushMatrix();
        if (absPos)
            GlStateManager.translate(-rm.viewerPosX, -rm.viewerPosY, -rm.viewerPosZ);
        GlStateManager.translate(posX, posY, posZ);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(viewerPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-0.025F, -0.025F, 0.025F);

        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);

        GlStateManager.translate(-fr.getStringWidth(str) / 2f, 0, 0);
        fr.drawString(str, 0, 0, 0xffffffff);

        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();

        GlStateManager.popMatrix();
    }

    private static String getMemberName(UUID uuid) {
        return getNameOrUuid(uuid, BattleStateClient.STATE.getMember(uuid));
    }

    private static String getMemberEntryName(Map.Entry<UUID, WeakReference<EntityLivingBase>> entry) {
        return getNameOrUuid(entry.getKey(), entry.getValue().get());
    }

    private static String getNameOrUuid(@Nullable UUID uuid, @Nullable EntityLivingBase entity) {
//        final EntityLivingBase entity = ref != null ? ref.get() : null;
        if (entity != null)
            return entity.getName();
        else if (uuid != null)
            return uuid.toString().substring(0, 6);
        else
            return "unknown";
    }
}
