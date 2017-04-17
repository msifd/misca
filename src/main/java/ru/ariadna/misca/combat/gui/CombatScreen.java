package ru.ariadna.misca.combat.gui;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import ru.ariadna.misca.gui.MiscaKeyBinds;

public class CombatScreen extends GuiScreen {
    public static final KeyBinding combatKey = MiscaKeyBinds.newKeyBind("combat", Keyboard.KEY_END);

    @Override
    public void initGui() {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(receiveCanceled = true)
    public void onKeyEvent(InputEvent.KeyInputEvent event) {
        if (combatKey.isPressed()) {
            FMLClientHandler.instance().showGuiScreen(this);
        }
    }
}
