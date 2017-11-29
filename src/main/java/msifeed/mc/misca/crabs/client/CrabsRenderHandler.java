package msifeed.mc.misca.crabs.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.mc.gui.im.ImGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class CrabsRenderHandler extends Gui {
    public static final CrabsRenderHandler INSTANCE = new CrabsRenderHandler();

    @SubscribeEvent
    public void onRenderGui(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;

        final Minecraft mc = Minecraft.getMinecraft();
        final EntityPlayer player = mc.thePlayer;

        

        final boolean fighting = false;

        ImGui imgui = ImGui.INSTANCE;
        imgui.newFrame();
        if (imgui.button(fighting ? "Stop fight" : "Start fight", 5, 5)) {

        }

        String debugInfo = String.format("");
        imgui.labelMultiline(debugInfo, 120, 5);
    }
}
