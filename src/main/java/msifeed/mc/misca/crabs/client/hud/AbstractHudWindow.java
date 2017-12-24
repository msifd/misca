package msifeed.mc.misca.crabs.client.hud;

import net.minecraft.client.settings.KeyBinding;

public abstract class AbstractHudWindow {
    boolean isOpened = false;

    abstract KeyBinding getKeyBind();
    abstract void open();
    abstract void close();
    abstract void render();
}
