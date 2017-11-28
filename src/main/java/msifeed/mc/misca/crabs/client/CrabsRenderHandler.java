package msifeed.mc.misca.crabs.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import msifeed.mc.gui.font.FontFactory;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import thvortex.betterfonts.StringCache;

public class CrabsRenderHandler extends Gui {
    public static final CrabsRenderHandler INSTANCE = new CrabsRenderHandler();

    StringCache fsexFont = FontFactory.createFsexFontRenderer();

    private CrabsRenderHandler() {
    }

    @SubscribeEvent
    public void onRenderGui(RenderGameOverlayEvent.Post event) {
        fsexFont.renderString("Шри\u00A76фты \u00A7nчада", 0, 20, 0xFFFFFFFF, false);
    }
}
