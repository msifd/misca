package ru.ariadna.misca.crabs.gui;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import ru.ariadna.misca.crabs.calculator.gui.CalculatorGuiScreen;

public class CrabsKeyHandler {
    //    private KeyBinding charKey = new KeyBinding("key.misca.char", Keyboard.KEY_C, "key.misca");
    private KeyBinding battleKey = new KeyBinding("key.misca.battle", Keyboard.KEY_N, "key.misca");

    public void onInit() {
//        ClientRegistry.registerKeyBinding(charKey);
        ClientRegistry.registerKeyBinding(battleKey);
        FMLCommonHandler.instance().bus().register(this);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onKeyPressed(InputEvent.KeyInputEvent event) {
        if (battleKey.isPressed()) {
            FMLCommonHandler.instance().showGuiScreen(new CalculatorGuiScreen());
        }
//        if (charKey.isPressed()) {
//            FMLCommonHandler.crabs().showGuiScreen(CharactersGUI.crabs);
//        }
//        if (battleKey.isPressed()) {
//            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
//            if (Crabs.crabs.fightManager.isInFight(player))
//                FMLCommonHandler.crabs().showGuiScreen(GuiScreenCombat.crabs);
//            else
//                FMLCommonHandler.crabs().showGuiScreen(GuiScreenLobby.crabs);
//        }
    }
}
