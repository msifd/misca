package msifeed.misca.combat.client;

import msifeed.misca.combat.Combat;
import msifeed.misca.combat.battle.Battle;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.ICombatant;
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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;
import java.util.stream.Collectors;

public class GuiCombatOverlay {
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

        final EntityPlayer player = Minecraft.getMinecraft().player;
        final Battle battle = Combat.MANAGER.getEntityBattle(player).orElse(null);
        if (battle == null) return;

        final String joinedQueue = battle.getQueue().stream()
                .map(uuid -> uuidToEntityName(player.world, uuid))
                .collect(Collectors.joining(", "));

        fr.drawString("queue: " + joinedQueue, 10, 10, 0xffffffff);
        fr.drawString("leader: " + uuidToEntityName(player.world, battle.getLeader()), 10, 10 + fr.FONT_HEIGHT, 0xffffffff);

        final ICombatant comb = CombatantProvider.get(player);
        if (comb.getTrainingHealth() > 0)
            fr.drawString("training hp: " + comb.getTrainingHealth(), 10, 10 + fr.FONT_HEIGHT * 2, 0xffffffff);
    }

    @SubscribeEvent
    public void onRenderEntity(RenderLivingEvent.Post<EntityLivingBase> event) {
        final EntityLivingBase entity = event.getEntity();
        final FontRenderer fr = event.getRenderer().getFontRendererFromRenderManager();

        final RenderManager rm = event.getRenderer().getRenderManager();
        float viewerYaw = rm.playerViewY;
        float viewerPitch = rm.playerViewX;

        GlStateManager.pushMatrix();
        GlStateManager.translate(event.getX(), event.getY() + event.getEntity().getEyeHeight() + 0.6, event.getZ());
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(viewerPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-0.025F, -0.025F, 0.025F);

        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);

        final String hpStr = String.format("%.0f/%.0f", entity.getHealth(), entity.getMaxHealth());
        fr.drawString(hpStr, 0, 0, 0xffffffff);

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
