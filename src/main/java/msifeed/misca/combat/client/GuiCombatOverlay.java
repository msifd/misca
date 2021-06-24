package msifeed.misca.combat.client;

import msifeed.misca.combat.Combat;
import msifeed.misca.combat.CombatEvent;
import msifeed.misca.combat.CombatFlow;
import msifeed.misca.combat.battle.Battle;
import msifeed.misca.combat.battle.BattleStateClient;
import msifeed.misca.combat.cap.CombatantProvider;
import msifeed.misca.combat.cap.ICombatant;
import msifeed.misca.combat.rules.Rules;
import msifeed.misca.combat.rules.WeaponInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Comparator;

public class GuiCombatOverlay {
    private static final long COUNTER_RESET_MS = 3000;
    private static final ArrayList<TimedEvent> events = new ArrayList<>();
    public static boolean COMBAT_DEBUG = false;

    public static void postEvent(TimedEvent te) {
        final TimedEvent existing = events.stream()
                .filter(e -> e.who.equals(te.who) && e.isMe == te.isMe && e.event == te.event)
                .findAny()
                .orElse(null);

        if (existing != null) {
            existing.start = te.start;
            existing.count += 1;
        } else {
            events.add(te);
        }

        events.sort(Comparator.comparing(e -> e.start));
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        final EntityPlayer player = Minecraft.getMinecraft().player;
        if (!CombatantProvider.get(player).isInBattle()) return;

        drawTopHud(event.getResolution());
        drawEvents(event.getResolution());

        if (COMBAT_DEBUG) {
            drawDebugInfo();
        }
    }

    private static void drawTopHud(ScaledResolution resolution) {
        final Battle state = BattleStateClient.STATE;

        final int frame = 32;
        final int gap = 2;
        final int barFullWidth = (int) state.getCombatants()
                .mapToDouble(CombatantsBarRender::getEntityWidth)
                .map(val -> val * frame + gap)
                .sum();

        final int[] posX = {(resolution.getScaledWidth() - barFullWidth) / 2};

        state.getCombatants().forEach(entity -> {
            if (entity == null) return;

            final int frameWidth = (int) (CombatantsBarRender.getEntityWidth(entity) * frame);
            CombatantsBarRender.renderModel(entity, posX[0], 0, frame, frameWidth);
            if (state.isStarted())
                CombatantsBarRender.renderBars(entity, posX[0], frame, frameWidth);

            posX[0] += frameWidth + gap;
        });
    }

    private static void drawEvents(ScaledResolution resolution) {
        final long now = Minecraft.getSystemTime();
        events.removeIf(c -> now - c.start > COUNTER_RESET_MS);

        final FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        int yPos = resolution.getScaledHeight() - 50;
        for (TimedEvent e : events) {
            final boolean isGood = e.event.isGood(e.isMe);
            final String msg = e.who + ' ' + e.event.tr()
                    + (e.count > 1 ? " x" + e.count : "");

            final int width = fr.getStringWidth(msg);
            int xPos = resolution.getScaledWidth() - width - 1;

            GuiScreen.drawRect(xPos - 1, yPos - 2, xPos + width + 1, yPos + fr.FONT_HEIGHT + 1, 0x99000000);

            final int color = isGood ? 0xff11ff11 : 0xffff1111;
            fr.drawString(msg, xPos, yPos, color);
            yPos -= fr.FONT_HEIGHT + 3;
        }

        GlStateManager.disableBlend();
    }

    private static void drawDebugInfo() {
        final Rules rules = Combat.getRules();
        final EntityLivingBase self = Minecraft.getMinecraft().player;
        final EntityLivingBase leader = BattleStateClient.STATE.getLeader();
        final Vec3d leaderPos = leader != null ? leader.getPositionVector() : self.getPositionVector();

        final EntityLivingBase actor = CombatFlow.getCombatActor(self);
        if (actor == null) return;
        final ICombatant com = CombatantProvider.get(actor);
        final WeaponInfo weapon = Combat.getWeapons().get(self, self.getHeldItemMainhand());

        final double moveAp = rules.movementActionPoints(actor, com.getPosition(), actor.getPositionVector());
        final double overheadAp = com.getActionPointsOverhead();
        final double attackAp = rules.attackActionPoints(actor, weapon) + overheadAp;
        final double usageAp = rules.usageActionPoints(weapon) + overheadAp;

        final String[] lines = {
                "actor: " + (CombatantProvider.get(self).hasPuppet() ? "\u00A74" : "") + actor.getName(),
                "health: " + String.format("%.2f/%.2f train hp: %.2f", actor.getHealth(), actor.getMaxHealth(), com.getTrainingHealth()),
                "neutral dmg: " + com.getNeutralDamage(),
                "----",
                "AP: " + String.format("%.2f; SPENT: %.2f; OVERHEAD: %.2f", com.getActionPoints(), com.getActionPointsSpent(), overheadAp),
                "MOVEMENT: " + formatActionPoints(moveAp, com.getActionPoints()),
                "ATTACK: " + formatActionPoints(attackAp, com.getActionPoints()),
                "USAGE : " + formatActionPoints(usageAp, com.getActionPoints()),
                "----",
                "YOU-LEAD COVER: " + rules.coverPoints(self, self.getPositionVector(), leaderPos),
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
    public static void onRenderOverlay(RenderWorldLastEvent event) {
        final EntityPlayer player = Minecraft.getMinecraft().player;
        final ICombatant com = CombatantProvider.get(player);
        if (!com.isInBattle()) return;

        renderTextAt("pos", com.getPosition().x, com.getPosition().y + 1.2, com.getPosition().z, true);
    }

    @SubscribeEvent
    public static void onRenderEntity(RenderLivingEvent.Post<EntityLivingBase> event) {
        final EntityPlayer self = Minecraft.getMinecraft().player;
        final EntityLivingBase entity = event.getEntity();
        if (self == entity) return;

        final ICombatant com = CombatantProvider.get(self);
        if (!com.isInBattle()) return;

        if (com.hasPuppet() && com.getPuppet() == entity.getEntityId()) {
            renderTextAt("puppet", event.getX(), event.getY() + event.getEntity().getEyeHeight() + 0.6, event.getZ(), false);
        }
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

    public static class TimedEvent {
        public String who;
        public boolean isMe;
        public CombatEvent event;
        public long start = Minecraft.getSystemTime();
        public int count = 1;
    }
}
