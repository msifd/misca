package msifeed.mc.misca.crabs;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import msifeed.mc.misca.crabs.battle.BattleManager;
import msifeed.mc.misca.crabs.character.CharacterManager;
import msifeed.mc.misca.crabs.character.CharacterProvider;
import msifeed.mc.misca.crabs.tools.ItemBattleStick;
import net.minecraftforge.common.MinecraftForge;

public class Crabs {
    @Subscribe
    public void preInit(FMLPreInitializationEvent event) {
        CharacterProvider.INSTANCE.preInit();
    }

    @Subscribe
    public void init(FMLInitializationEvent event) {
        CrabsNetwork.INSTANCE.onInit();
        BattleManager.INSTANCE.onInit();
        CharacterManager.INSTANCE.onInit();

        ItemBattleStick battleStick = new ItemBattleStick();
        MinecraftForge.EVENT_BUS.register(battleStick);
        GameRegistry.registerItem(battleStick, "battle_stick");
    }
}
