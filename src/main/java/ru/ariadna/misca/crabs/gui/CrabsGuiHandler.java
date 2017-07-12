package ru.ariadna.misca.crabs.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import ru.ariadna.misca.gui.MiscaGuiHandler;

import java.util.stream.IntStream;

public class CrabsGuiHandler implements IGuiHandler {
    private static final int ID_SHIFT = 100;

    public void onInit() {
        MiscaGuiHandler.instance.register(this, IntStream.range(ID_SHIFT, ID_SHIFT + GuiTypes.values().length).toArray());
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (GuiTypes.values()[ID - ID_SHIFT]) {
            case LOBBY:
                return new GuiScreenLobby();
            case COMBAT:
                return new GuiScreenCombat();
        }
        return null;
    }

    public enum GuiTypes {
        LOBBY, COMBAT;

        public int id() {
            return this.ordinal() + ID_SHIFT;
        }
    }
}
