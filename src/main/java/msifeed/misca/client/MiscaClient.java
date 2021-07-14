package msifeed.misca.client;

import msifeed.misca.MiscaConfig;
import msifeed.misca.combat.client.GuiCombatOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
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

    public static KeyBinding charsheetKey = new KeyBinding("key.misca.charsheet", KeyConflictContext.IN_GAME, KeyModifier.SHIFT, Keyboard.KEY_I, "key.categories.misca");
    public static KeyBinding effortsKey = new KeyBinding("key.misca.effort", KeyConflictContext.IN_GAME, Keyboard.KEY_I, "key.categories.misca");
    public static KeyBinding combatKey = new KeyBinding("key.misca.combat", KeyConflictContext.IN_GAME, Keyboard.KEY_O, "key.categories.misca");

    public void preInit() {
        Display.setTitle(MiscaConfig.client.windowTitle);
    }

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(GuiCombatOverlay.class);

        MiscaTheme.load();
        ClientRegistry.registerKeyBinding(charsheetKey);
        ClientRegistry.registerKeyBinding(effortsKey);
        ClientRegistry.registerKeyBinding(combatKey);
    }

    @SubscribeEvent
    void onKeyTyped(InputEvent.KeyInputEvent event) {
        if (charsheetKey.isPressed())
            Minecraft.getMinecraft().displayGuiScreen(new ScreenCharsheet(Minecraft.getMinecraft().player));
        else if (combatKey.isPressed())
            Minecraft.getMinecraft().displayGuiScreen(new ScreenCombat());
        else if (effortsKey.isPressed())
            Minecraft.getMinecraft().displayGuiScreen(new ScreenEffortRoll(Minecraft.getMinecraft().player));
    }
}
