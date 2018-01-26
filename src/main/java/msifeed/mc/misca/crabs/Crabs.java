package msifeed.mc.misca.crabs;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import msifeed.mc.misca.crabs.action.ActionManager;
import msifeed.mc.misca.crabs.action.ActionProvider;
import msifeed.mc.misca.crabs.character.CharacterManager;
import msifeed.mc.misca.crabs.character.CharacterProvider;
import msifeed.mc.misca.crabs.context.ContextManager;
import msifeed.mc.misca.crabs.fight.FightManager;
import msifeed.mc.misca.crabs.tools.ItemBattleStick;
import msifeed.mc.misca.crabs.tools.ItemCharSheet;
import msifeed.mc.misca.crabs.tools.ItemHealthController;
import net.minecraftforge.common.MinecraftForge;

public class Crabs {
    public void preInit(FMLPreInitializationEvent event) {
        CharacterProvider.INSTANCE.preInit();
        ActionProvider.INSTANCE.preInit();
    }

    public void init(FMLInitializationEvent event) {
        CrabsNetwork.INSTANCE.onInit();
        ContextManager.INSTANCE.onInit();
        CharacterManager.INSTANCE.onInit();
        ActionManager.INSTANCE.onInit();
        FightManager.INSTANCE.onInit();

        ItemBattleStick battleStick = new ItemBattleStick();
        MinecraftForge.EVENT_BUS.register(battleStick);
        GameRegistry.registerItem(battleStick, "battle_stick");

        ItemCharSheet charSheet = new ItemCharSheet();
        MinecraftForge.EVENT_BUS.register(charSheet);
        GameRegistry.registerItem(charSheet, "char_sheet");

        ItemHealthController healthStick = new ItemHealthController();
        MinecraftForge.EVENT_BUS.register(healthStick);
        GameRegistry.registerItem(healthStick, "health_controller");
    }
}
