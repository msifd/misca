package msifeed.misca;

import msifeed.misca.charsheet.client.GuiCharsheet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

@SideOnly(Side.CLIENT)
public enum MiscaClient {
    INSTANCE;

    public static KeyBinding charsheetKey = new KeyBinding("key.misca.charsheet", Keyboard.KEY_I, "key.categories.misca");

    public void preInit() {
        Display.setTitle(MiscaConfig.windowTitle);
    }

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);

        ClientRegistry.registerKeyBinding(charsheetKey);
    }

    @SubscribeEvent
    void onKeyTyped(InputEvent.KeyInputEvent event) {
        if (charsheetKey.isPressed())
            Minecraft.getMinecraft().displayGuiScreen(new GuiCharsheet(Minecraft.getMinecraft().player));
    }
}
