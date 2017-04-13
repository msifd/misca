package ru.ariadna.misca.combat;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ariadna.misca.Misca;
import ru.ariadna.misca.combat.calculation.CalcRulesProvider;
import ru.ariadna.misca.combat.characters.CharacterProvider;
import ru.ariadna.misca.combat.commands.CommandCharParams;
import ru.ariadna.misca.combat.commands.CommandCombat;
import ru.ariadna.misca.combat.commands.CommandDice;

import java.io.File;

public class Combat {
    public static Logger logger = LogManager.getLogger("Misca-Combat");
    private File configDir;

    private CharacterProvider characterProvider = new CharacterProvider();
    private CalcRulesProvider rulesProvider = new CalcRulesProvider();
    private CombatManager combatManager = new CombatManager(rulesProvider);

    private CommandCharParams commandCharParams = new CommandCharParams(characterProvider);
    private CommandCombat commandCombat = new CommandCombat(characterProvider, combatManager);
    private CommandDice commandDice = new CommandDice(characterProvider);

    public void init(FMLServerStartingEvent event) {
        configDir = new File(Misca.config_dir, "combat");

        characterProvider.init();
        combatManager.init();
        event.registerServerCommand(commandCharParams);
        event.registerServerCommand(commandCombat);
        event.registerServerCommand(commandDice);
    }
}
