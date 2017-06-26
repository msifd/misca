package ru.ariadna.misca.crabs.lobby;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.Map;

public class LobbyManager {

    private Map<EntityLiving, Lobby> entityToLobby = new HashMap<>();

    public void onInit(FMLInitializationEvent event) {
        ItemBattleFlag itemBattleFlag = new ItemBattleFlag();
        MinecraftForge.EVENT_BUS.register(itemBattleFlag);
        GameRegistry.registerItem(itemBattleFlag, "battle_flag");
    }

}
