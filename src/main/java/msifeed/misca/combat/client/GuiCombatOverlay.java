package msifeed.misca.combat.client;

import msifeed.misca.combat.Combat;
import msifeed.misca.combat.battle.Battle;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.ICombatant;
import msifeed.misca.combat.rules.Rules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;
import java.util.stream.Collectors;

public class GuiCombatOverlay {
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;


        final EntityPlayer player = Minecraft.getMinecraft().player;
        if (player.getHeldItemMainhand().isEmpty()) return;

        final Battle battle = Combat.MANAGER.getEntityBattle(player);
        if (battle == null) return;

        final ICombatant com = CombatantProvider.get(player);
        final String joinedQueue = battle.getQueue().stream()
                .map(uuid -> uuidToEntityName(player.world, uuid))
                .collect(Collectors.joining(", "));
        final String joinedMembers = battle.getMembers().stream()
                .map(uuid -> uuidToEntityName(player.world, uuid))
                .collect(Collectors.joining(", "));
        final double moveAp = Rules.movementActionPoints(com.getPosition(), player.getPositionVector());
        final double attackAp = Rules.attackActionPoints(player);

        final String[] lines = {
                "queue: " + joinedQueue,
                "leader: " + uuidToEntityName(player.world, battle.getLeader()),
                "members: " + joinedMembers,
                "----",
                "action points: " + com.getActionPoints(),
                "pos: " + com.getPosition(),
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

    private static String uuidToEntityName(World w, UUID uuid) {
        if (uuid == null) return "null";
        return w.loadedEntityList.stream()
                .filter(e -> e instanceof EntityLivingBase)
                .filter(e -> e.getUniqueID().equals(uuid))
                .findAny()
                .map(Entity::getName)
                .orElse(uuid.toString());
    }
}
