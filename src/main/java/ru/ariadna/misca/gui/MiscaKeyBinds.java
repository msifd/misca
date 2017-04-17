package ru.ariadna.misca.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class MiscaKeyBinds {
    private static List<KeyBinding> bindings = new ArrayList<>();

    @SideOnly(Side.CLIENT)
    public static KeyBinding newKeyBind(String description, int key) {
        KeyBinding bind = new KeyBinding("misca.keys." + description, key, "misca.title");
        bindings.add(bind);
        return bind;
    }

    @SideOnly(Side.CLIENT)
    public static void register() {
        KeyBinding[] old_binds = Minecraft.getMinecraft().gameSettings.keyBindings;
        KeyBinding[] new_binds = bindings.toArray(new KeyBinding[0]);
        Minecraft.getMinecraft().gameSettings.keyBindings = ArrayUtils.addAll(old_binds, new_binds);
    }
}
