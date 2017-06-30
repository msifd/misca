package ru.ariadna.misca.gui;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import ru.ariadna.misca.Misca;

import java.util.TreeMap;

public class MiscaGuiHandler implements IGuiHandler {
    public static final MiscaGuiHandler instance = new MiscaGuiHandler();

    private TreeMap<Integer, IGuiHandler> delegates = new TreeMap<>();

    @Subscribe
    public void onInit(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(Misca.instance(), instance);
    }

    public void register(IGuiHandler del, int[] ids) {
        for (int id : ids) {
            delegates.put(id, del);
        }
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return delegates.get(ID).getServerGuiElement(ID, player, world, x, y, z);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return delegates.get(ID).getClientGuiElement(ID, player, world, x, y, z);
    }
}
