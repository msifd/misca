package msifeed.mc.misca.crabs.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.mc.gui.im.ImGui;
import msifeed.mc.misca.crabs.EntityUtils;
import msifeed.mc.misca.crabs.battle.BattleManager;
import msifeed.mc.misca.crabs.battle.BattleNetwork;
import msifeed.mc.misca.crabs.battle.FighterAction;
import msifeed.mc.misca.crabs.battle.FighterContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import org.lwjgl.opengl.GL11;

public class CrabsRenderHandler extends Gui {
    public static final CrabsRenderHandler INSTANCE = new CrabsRenderHandler();

    @SubscribeEvent
    public void onRenderGui(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;

        final Minecraft mc = Minecraft.getMinecraft();
        final EntityPlayer player = mc.thePlayer;
        final BattleManager bm = BattleManager.INSTANCE;
        final FighterContext context = bm.getContext(player.getUniqueID());

        final boolean inBattle = context != null;

        ImGui imgui = ImGui.INSTANCE;
        imgui.newFrame();

        if (imgui.button(inBattle ? "Stop fight" : "Start fight", 5, 5)) {
            FighterAction action = new FighterAction(inBattle ? FighterAction.Type.LEAVE : FighterAction.Type.JOIN);
            BattleNetwork.INSTANCE.notifyServer(action);
        }

        if (inBattle) {
            if (imgui.button("Punch", 5, 30)) {
                BattleNetwork.INSTANCE.notifyServer(new FighterAction(FighterAction.Type.MOVE));
            }
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String contextStr = gson.toJson(BattleManager.INSTANCE.getContexts());

        String debugInfo = String.format("inBattle: %b\nme: %s\ncontexts: %s", inBattle, player.getUniqueID(), contextStr);
        imgui.labelMultiline(debugInfo, 120, 5);
    }

    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Post event) {
        EntityPlayer self = Minecraft.getMinecraft().thePlayer;
        if (event.entity == self) return;

        final FighterContext self_ctx = BattleManager.INSTANCE.getContext(self.getUniqueID());
        final FighterContext entity_ctx = BattleManager.INSTANCE.getContext(event.entity);
        if (self_ctx == null || entity_ctx == null) return;

        final boolean isUnderMyControl = self_ctx.control == entity_ctx.uuid;
        final int color = isUnderMyControl ? 0x808020 : 0x802020;

        // Рендер плашки с режимом боя над энтити
        RenderManager renderManager = RenderManager.instance;
        float f = 1.6F;
        float f1 = 0.016666668F * f;
        GL11.glPushMatrix();
        GL11.glTranslatef((float) event.x, (float) event.y + event.entity.height + 1F, (float) event.z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-f1, -f1, f1);

        GL11.glDisable(GL11.GL_LIGHTING);
//        GL11.glDisable(GL11.GL_DEPTH_TEST);
//        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        final double icon_half = 5;

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_I(color, 200);
//        tessellator.setColorRGBA_F(0.5F, 0.1F, 0.1F, 0.8F);
        tessellator.addVertex(-icon_half, -icon_half, 0.0D);
        tessellator.addVertex(-icon_half, icon_half, 0.0D);
        tessellator.addVertex(icon_half, icon_half, 0.0D);
        tessellator.addVertex(icon_half, -icon_half, 0.0D);
        tessellator.draw();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
//        GL11.glEnable(GL11.GL_DEPTH_TEST);
//        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }
}
