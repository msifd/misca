package msifeed.misca.combat.client;

import msifeed.misca.MiscaConfig;
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
        final BattleStateClient state = BattleStateClient.INSTANCE;

        final Vec3d pos = com.getPosition();

        final String joinedMembers = state.getMembers().entrySet().stream()
                .map(GuiCombatOverlay::getMemberEntryName)
                .collect(Collectors.joining(", "));
        final String joinedQueue = state.getQueue().stream()
                .map(GuiCombatOverlay::getMemberName)
                .collect(Collectors.joining(", "));
        final String leaderName = getNameOrUuid(state.getLeaderUuid(), state.getMember(state.getLeaderUuid()));
        final String posStr = String.format("x: %.2f, y: %.2f, z: %.2f", pos.x, pos.y, pos.z);
        final double moveAp = Rules.movementActionPoints(com.getPosition(), player.getPositionVector());
        final double attackAp = Rules.attackActionPoints(player);

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
                "ATTACK: " + formatActionPoints(attackAp, com.getActionPoints()),
                "TOTAL: " + formatActionPoints(moveAp + attackAp, com.getActionPoints()) + " / " + com.getActionPoints()
        };

        final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        for (int i = 0; i < lines.length; i++)
            fr.drawString(lines[i], 10, 10 + fr.FONT_HEIGHT * i, 0xffffffff);
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

        final String hpStr = String.format("%.1f/%.1f", entity.getHealth(), entity.getMaxHealth());
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
        return getNameOrUuid(uuid, BattleStateClient.INSTANCE.getMember(uuid));
    }

    private static String getMemberEntryName(Map.Entry<UUID, WeakReference<EntityLivingBase>> entry) {
        return getNameOrUuid(entry.getKey(), entry.getValue());
    }

    private static String getNameOrUuid(@Nullable UUID uuid, @Nullable WeakReference<EntityLivingBase> ref) {
        final EntityLivingBase entity = ref != null ? ref.get() : null;
        if (entity != null)
            return entity.getName();
        else if (uuid != null)
            return uuid.toString().substring(0, 6);
        else
            return "unknown";
    }
}
