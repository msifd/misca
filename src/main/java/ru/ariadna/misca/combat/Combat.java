package ru.ariadna.misca.combat;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ariadna.misca.Misca;
import ru.ariadna.misca.combat.calculation.CalcRulesProvider;
import ru.ariadna.misca.combat.calculation.Calculon;
import ru.ariadna.misca.combat.characters.CharacterProvider;
import ru.ariadna.misca.combat.commands.*;
import ru.ariadna.misca.combat.fight.FightManager;
import ru.ariadna.misca.combat.lobby.LobbyManager;

import java.io.File;

public class Combat {
    public static Logger logger = LogManager.getLogger("Misca-Combat");
    public static File configDir;

    private CharacterProvider characterProvider = new CharacterProvider();
    private CalcRulesProvider rulesProvider = new CalcRulesProvider();
    private Calculon calculon = new Calculon(rulesProvider);
    private FightManager fightManager = new FightManager(characterProvider, calculon);
    private LobbyManager lobbyManager = new LobbyManager(fightManager);

    private CommandMiscaCombat commandMiscaCombat = new CommandMiscaCombat(characterProvider, rulesProvider);
    private CommandCharStats commandCharStats = new CommandCharStats(characterProvider);
    private CommandCombatLobby commandCombatLobby = new CommandCombatLobby(lobbyManager);
    private CommandCombat commandCombat = new CommandCombat(fightManager, rulesProvider);
    private CommandDice commandDice = new CommandDice(characterProvider);

    public void preInit(FMLPreInitializationEvent event) {
        configDir = new File(Misca.config_dir, "combat");
        configDir.mkdirs();
    }

    public void init(FMLInitializationEvent event) {
        characterProvider.init();
        rulesProvider.init();
        calculon.init();
        fightManager.init();
    }

    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(commandMiscaCombat);
        event.registerServerCommand(commandCharStats);
        event.registerServerCommand(commandCombatLobby);
        event.registerServerCommand(commandCombat);
        event.registerServerCommand(commandDice);
    }
}
