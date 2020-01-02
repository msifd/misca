package msifeed.mc.misca.tweaks;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import msifeed.mc.misca.config.ConfigManager;
import msifeed.mc.misca.config.JsonConfig;
import org.lwjgl.opengl.Display;

public enum GameWindowOptions {
    INSTANCE;

    private JsonConfig<Content> config = ConfigManager.getConfigFor(Content.class, "window.json");

    public static void preInit() {
        FMLCommonHandler.instance().bus().register(INSTANCE);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            setTitle();
    }

    private void setTitle() {
        final Content content = config.get();
        if (content == null)
            return;
        final String title = content.title;
        if (!Display.getTitle().equals(title))
            Display.setTitle(title);
    }

    public static class Content {
        public String title = "Morgana";
    }
}
