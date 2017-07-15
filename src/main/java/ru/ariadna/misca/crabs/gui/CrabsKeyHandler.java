package ru.ariadna.misca.crabs.gui;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;
import ru.ariadna.misca.crabs.Crabs;
import ru.ariadna.misca.crabs.characters.gui.CharactersGUI;

public class CrabsKeyHandler {
    private KeyBinding charKey = new KeyBinding("key.misca.char", Keyboard.KEY_C, "key.misca");
    private KeyBinding battleKey = new KeyBinding("key.misca.battle", Keyboard.KEY_B, "key.misca");

    public void onInit() {
        ClientRegistry.registerKeyBinding(charKey);
        ClientRegistry.registerKeyBinding(battleKey);
        FMLCommonHandler.instance().bus().register(this);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onKeyPressed(InputEvent.KeyInputEvent event) {
        if (charKey.isPressed()) {
            FMLCommonHandler.instance().showGuiScreen(CharactersGUI.instance);
        }
        if (battleKey.isPressed()) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            if (Crabs.instance.fightManager.isInFight(player))
                FMLCommonHandler.instance().showGuiScreen(GuiScreenCombat.instance);
            else
                FMLCommonHandler.instance().showGuiScreen(GuiScreenLobby.instance);
        }
    }
}
