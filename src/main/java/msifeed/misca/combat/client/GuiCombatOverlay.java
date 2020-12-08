package msifeed.misca.combat.client;

import msifeed.mellow.render.RenderShapes;
import msifeed.mellow.sprite.FlatSprite;
import msifeed.mellow.utils.Geom;
import msifeed.misca.client.MiscaConfig;
import msifeed.misca.combat.battle.Battle;
import msifeed.misca.combat.battle.BattleStateClient;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.ICombatant;
import msifeed.misca.combat.rules.Rules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
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

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (!MiscaConfig.combatDebug) return;
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        final EntityPlayer player = Minecraft.getMinecraft().player;
//        if (player.getHeldItemMainhand().isEmpty()) return;

        final ICombatant com = CombatantProvider.get(player);
        if (!com.isInBattle()) return;
        final Battle state = BattleStateClient.STATE;

        final Vec3d pos = com.getPosition();

        final String joinedMembers = state.getMembers().entrySet().stream()
                .map(GuiCombatOverlay::getMemberEntryName)
                .collect(Collectors.joining(", "));
        final String joinedQueue = state.getQueue().stream()
                .map(GuiCombatOverlay::getMemberName)
                .collect(Collectors.joining(", "));
        final String leaderName = getNameOrUuid(state.getLeaderUuid(), state.getLeader());
        final String posStr = String.format("x: %.2f, y: %.2f, z: %.2f", pos.x, pos.y, pos.z);
        final double moveAp = Rules.movementActionPoints(com.getPosition(), player.getPositionVector());
        final double overheadAp = com.getActionPointsOverhead();
        final double attackAp = Rules.attackActionPoints(player) + overheadAp;

        final String[] lines = {
                "members: " + joinedMembers,
                "queue: " + joinedQueue,
                "leader: " + leaderName,
                "----",
                "action points: " + String.format("%.2f", com.getActionPoints()),
                "pos: " + posStr,
                "training hp: " + com.getTrainingHealth(),
                "----",
                "MOVEMENT: " + formatActionPoints(moveAp, com.getActionPoints()),
                "ATTACK: " + formatActionPoints(attackAp, com.getActionPoints()) + String.format(" (overhead: %.2f)", overheadAp),
                "TOTAL: " + formatActionPoints(moveAp + attackAp, com.getActionPoints())
                        + String.format(" / %.2f", com.getActionPoints())
        };

        final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        for (int i = 0; i < lines.length; i++)
            fr.drawString(lines[i], 10, 10 + fr.FONT_HEIGHT * i, 0xffffffff);

        drawNewHud();
    }

    private void drawNewHud() {
        final Battle state = BattleStateClient.STATE;

        final int[] xOffset = {150};
        state.getCombatants().forEach(entity -> {
            final FlatSprite face = EntityFaceSprites.INSTANCE.getFaceSprite(entity);
            if (face == null) return;

            face.render(new Geom(xOffset[0] + 1, 3, 24, 24));
            CombatTheme.combatantFrame.render(new Geom(xOffset[0], 2, 26, 31));

            xOffset[0] += 26 + 1;
        });

        // action points bar

        final EntityPlayer player = Minecraft.getMinecraft().player;
        final ICombatant com = CombatantProvider.get(player);

        final double fullAp = com.getActionPoints();
        final double moveAp = Rules.movementActionPoints(com.getPosition(), player.getPositionVector());
        final double actionAp = Rules.attackActionPoints(player);
        final double ap = Math.max(com.getActionPoints() - moveAp, 0);

        final int xPos = 5, yPos = 5;
        final int bgColor = ap > 0 ? 0xffffffff : 0xffff0000;
        final int barWidth = 100;
        final double pxPerAp = barWidth / fullAp;

        final Geom geom = new Geom(xPos - 1, yPos - 1, barWidth + 2, 7);
        RenderShapes.rect(geom, bgColor);

        geom.set(xPos, yPos, (int) (ap * pxPerAp), 5);
        RenderShapes.rect(geom, 0xff000000);

        geom.setSize(1, 5);
        double consumedAp = 0;
        double nextAp = actionAp + com.getActionPointsOverhead();
        while (ap >= consumedAp + nextAp) {
            geom.x = (int) (xPos + (consumedAp + nextAp) * pxPerAp);
            RenderShapes.rect(geom, bgColor);

            consumedAp += nextAp;
            nextAp += nextAp / 2;
        }
    }

    private String formatActionPoints(double ap, double total) {
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

        renderTextAt("pos", com.getPosition().x, com.getPosition().y + 1, com.getPosition().z, true);
    }

    @SubscribeEvent
    public void onRenderEntity(RenderLivingEvent.Post<EntityLivingBase> event) {
        if (!MiscaConfig.combatDebug) return;
        final EntityLivingBase entity = event.getEntity();

        final ICombatant com = CombatantProvider.get(entity);
        final double atkAp = Rules.attackActionPoints(entity) + com.getActionPointsOverhead();
        final double movAp = Rules.movementActionPoints(com.getPosition(), entity.getPositionVector());
        final String apStr = String.format("ap: %.1f, atk: %.1f, mov: %.1f", com.getActionPoints(), atkAp, movAp);
        renderTextAt(apStr, event.getX(), event.getY() + event.getEntity().getEyeHeight() + 0.8, event.getZ(), false);

        final String hpStr = String.format("hp: %.1f/%.1f", entity.getHealth(), entity.getMaxHealth());
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
