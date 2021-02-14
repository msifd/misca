package msifeed.misca.combat.client;

import msifeed.mellow.render.RenderUtils;
import msifeed.misca.client.MiscaConfig;
import msifeed.misca.combat.Combat;
import msifeed.misca.combat.battle.Battle;
import msifeed.misca.combat.battle.BattleStateClient;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.ICombatant;
import msifeed.misca.combat.rules.Rules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class GuiCombatOverlay {
    private final CombatantFrameView frame = new CombatantFrameView();
    private final ActionPointsBarView bar = new ActionPointsBarView();

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (!MiscaConfig.combatDebug) return;
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        final EntityPlayer player = Minecraft.getMinecraft().player;
        if (!CombatantProvider.get(player).isInBattle()) return;

        drawTextInfo();
        drawHud();
    }

    private void drawHud() {
        final Battle state = BattleStateClient.STATE;

        final ScaledResolution resolution = RenderUtils.getScaledResolution();
        final int pxPerFrame = frame.getRenderGeom().w + 1;
        final int frameOffset = (resolution.getScaledWidth() - state.getQueue().size() * pxPerFrame) / 2;

        frame.setPos(0, 0, 0);
        frame.translate(frameOffset, 0, 0);

        state.getCombatants().forEach(entity -> {
            frame.setFace(entity);
            frame.render(frame.getRenderGeom());
            frame.translate(pxPerFrame, 0, 0);
        });

        bar.setPos(0, 0, 0);
        bar.translate(frameOffset - bar.getRenderGeom().w - 5, 5, 0);
        bar.render(bar.getRenderGeom());
    }

    private static void drawTextInfo() {
        final EntityPlayer player = Minecraft.getMinecraft().player;
        final ICombatant selfCom = CombatantProvider.get(player);

        final EntityLivingBase entity;
        if (selfCom.hasPuppet()) {
            final Entity e = player.world.getEntityByID(selfCom.getPuppet());
            if (!(e instanceof EntityLivingBase)) return;
            entity = (EntityLivingBase) e;
        } else {
            entity = player;
        }
        final ICombatant com = CombatantProvider.get(entity);

        final Battle state = BattleStateClient.STATE;
        final Rules rules = Combat.getRules();

        final Vec3d pos = com.getPosition();
        final String joinedMembers = state.getMembers().entrySet().stream()
                .map(GuiCombatOverlay::getMemberEntryName)
                .collect(Collectors.joining(", "));
        final String joinedQueue = state.getQueue().stream()
                .map(GuiCombatOverlay::getMemberName)
                .collect(Collectors.joining(", "));
        final String leaderName = getNameOrUuid(state.getLeaderUuid(), state.getLeader());
        final String posStr = String.format("x: %.2f, y: %.2f, z: %.2f", pos.x, pos.y, pos.z);
        final double moveAp = rules.movementActionPoints(com.getPosition(), entity.getPositionVector());
        final double overheadAp = com.getActionPointsOverhead();
        final double attackAp = rules.attackActionPoints(entity) + overheadAp;
        final double usageAp = rules.usageActionPoints(entity.getHeldItemMainhand().getItem()) + overheadAp;

        final String[] lines = {
                "members: " + joinedMembers,
                "queue: " + joinedQueue,
                "leader: " + leaderName,
                "your puppet: " + selfCom.getPuppet(),
                "----",
                "action points: " + String.format("%.2f", com.getActionPoints()),
                "pos: " + posStr,
                "training hp: " + com.getTrainingHealth(),
                "neutral dmg: " + com.getNeutralDamage(),
                "----",
                "AP OVERHEAD: " + String.format("%.2f", overheadAp),
                "MOVEMENT: " + formatActionPoints(moveAp, com.getActionPoints()),
                "ATTACK: " + String.format("%s/%.2f", formatActionPoints(attackAp, com.getActionPoints()), com.getActionPoints()),
                "USAGE_: " + String.format("%s/%.2f", formatActionPoints(usageAp, com.getActionPoints()), com.getActionPoints()),
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

    @SubscribeEvent
    public void onRenderEntity(RenderLivingEvent.Post<EntityLivingBase> event) {
        if (!MiscaConfig.combatDebug) return;

        final EntityPlayer self = Minecraft.getMinecraft().player;
        final EntityLivingBase entity = event.getEntity();
        if (self == entity) return;

        if (!CombatantProvider.get(self).isInBattle()) return;

        final ICombatant com = CombatantProvider.get(entity);
        if (!com.isInBattle()) return;

        {
            final double cover = Combat.getRules().coverBlocks(self.world, self.getPositionVector(), entity.getPositionVector());
            final String coverStr = String.format("your cov: %.1f", cover);
            renderTextAt(coverStr, event.getX(), event.getY() + event.getEntity().getEyeHeight() + 1.2, event.getZ(), false);
        }
        {
            final double cover = Combat.getRules().coverBlocks(self.world, entity.getPositionVector(), self.getPositionVector());
            final String coverStr = String.format("its cov: %.1f", cover);
            renderTextAt(coverStr, event.getX(), event.getY() + event.getEntity().getEyeHeight() + 1.0, event.getZ(), false);
        }

        final Rules rules = Combat.getRules();
        final double atkAp = rules.attackActionPoints(entity) + com.getActionPointsOverhead();
        final double movAp = rules.movementActionPoints(com.getPosition(), entity.getPositionVector());
        final String apStr = String.format("ap: %.1f, atk: %.1f, mov: %.1f", com.getActionPoints(), atkAp, movAp);
        renderTextAt(apStr, event.getX(), event.getY() + event.getEntity().getEyeHeight() + 0.8, event.getZ(), false);

        final String hpStr = String.format("hp: %.1f/%.1f, neu dmg: %.1f", entity.getHealth(), entity.getMaxHealth(), com.getNeutralDamage());
        renderTextAt(hpStr, event.getX(), event.getY() + event.getEntity().getEyeHeight() + 0.6, event.getZ(), false);
    }

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
