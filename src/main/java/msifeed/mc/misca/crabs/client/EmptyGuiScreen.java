package msifeed.mc.misca.crabs.client;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.gui.GuiScreen;

public class EmptyGuiScreen extends GuiScreen {
    public static EmptyGuiScreen INSTANCE = new EmptyGuiScreen();

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void handleKeyboardInput() {
        super.handleKeyboardInput();
        FMLCommonHandler.instance().fireKeyInput();
    }
}
