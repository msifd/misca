package msifeed.mc.misca.crabs.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.mc.gui.im.ImGui;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class CrabsRenderHandler extends Gui {
    public static final CrabsRenderHandler INSTANCE = new CrabsRenderHandler();

    private boolean fighting = false;

    @SubscribeEvent
    public void onRenderGui(RenderGameOverlayEvent.Post event) {
        ImGui gui = ImGui.INSTANCE;
        gui.newFrame();
        if (gui.button(fighting ? "Stop fight" : "Start fight", 5, 5)) {
            fighting = !fighting;
        }
    }
}
