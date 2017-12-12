package msifeed.mc.misca.crabs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;

public final class CrabsKeyBinds {
    // TODO наполнить lang файл
    public static final KeyBinding charHud = new KeyBinding("key.misca.char_hud", Keyboard.KEY_C, "key.misca");
    public static final KeyBinding battleHud = new KeyBinding("key.misca.battle_hud", Keyboard.KEY_B, "key.misca");

    public static void register() {
        Minecraft.getMinecraft().gameSettings.keyBindings = ArrayUtils.addAll(
                new KeyBinding[]{battleHud},
                Minecraft.getMinecraft().gameSettings.keyBindings);
    }
}
