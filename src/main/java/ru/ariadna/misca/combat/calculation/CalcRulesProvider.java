package ru.ariadna.misca.combat.calculation;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import ru.ariadna.misca.combat.Combat;
import ru.ariadna.misca.combat.fight.Action;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CalcRulesProvider {
    private File dataFile;
    private Toml toml = new Toml();

    private Map<Action, CalcRule> system_rules = new EnumMap<>(Action.class);
    private Map<Action, CalcRule> attack_rules = new EnumMap<>(Action.class);
    private Map<Action, CalcRule> defence_rules = new EnumMap<>(Action.class);

    public void init() {
        dataFile = new File(Combat.configDir, "combat_rules.toml");
        if (!dataFile.exists()) {
            writeDefaultRules();
        }
        reload();
    }

    public CalcRule getRule(Action act, Action.Stage stage) {
        switch (stage) {
            case SYSTEM:
                return system_rules.get(act);
            case ATTACK:
                return attack_rules.get(act);
            case DEFENCE:
                return defence_rules.get(act);
        }
        return null;
    }

    public boolean reload() {
        if (dataFile.exists()) {
            Combat.logger.error("Combat rules file not found!");
        }

        try {
            RulesFileContent content = toml.read(dataFile).to(RulesFileContent.class);
            // Fix empty config file
            if (content == null) {
                content = getDefaultRules();
            }
            system_rules = makeRules(content.system.entrySet());
            attack_rules = makeRules(content.attack.entrySet());
            defence_rules = makeRules(content.defence.entrySet());

        } catch (IllegalStateException e) {
            Combat.logger.error("Error while reading combat rules config! {}", e);
            return false;
        }
        return true;
    }

    private Map<Action, CalcRule> makeRules(Set<Map.Entry<String, String>> raw_rules) {
        Map<Action, CalcRule> tmp = new EnumMap<>(Action.class);
        for (Map.Entry<String, String> raw_rule : raw_rules) {
            try {
                Action act = Action.valueOf(raw_rule.getKey().toUpperCase());
                CalcRule rule = new CalcRule(raw_rule.getValue());
                tmp.put(act, rule);
            } catch (IllegalArgumentException e) {
                Combat.logger.error("Error while parsing combat rules! Wrong action name: {}", raw_rule.getKey());
            } catch (CalcRule.RuleParseException e) {
                Combat.logger.error("Error while parsing combat rules for action '{}'! {}", raw_rule.getKey(), e);
            }
        }
        return tmp;
    }

    private void writeDefaultRules() {
        try {
            TomlWriter tomlWriter = new TomlWriter();
            tomlWriter.write(getDefaultRules(), dataFile);
        } catch (IOException e) {
            Combat.logger.error("Failed to write default rules! {}", e);
        }
    }

    private RulesFileContent getDefaultRules() {
        RulesFileContent file = new RulesFileContent();
        file.system.put("init", "d20 ref*0.5 det*0.5");

        file.attack.put("hit", "d10 str*1.5 end*0.5");
        file.attack.put("shoot", "d10 per*1.5 ref*0.5");
        file.attack.put("magic", "d10 spr*1.5 wis*0.5");
        file.attack.put("slam", "d10 end*0.5 ref*0.5 det*0.5");
        file.attack.put("other", "d10 str*0.5 per*0.5 ref*0.5 det*0.5");
        file.attack.put("special", "d10 wis*1.5 spr*0.5");
        file.attack.put("flee", "d10 per*0.5 ref*0.5 end*0.5 det*0.5 wis*0.5 spr*0.5");
        file.attack.put("safe", "d10 det*1.5 end*0.5");

        file.defence.put("defence", "d10 end*1.5 str*0.5");
        file.defence.put("dodge", "d10 ref*1.5 per*0.5");
        file.defence.put("magic", "d10 spr*1 wis*1");
        file.defence.put("dum", "d10 str*0.5 per*0.5 det*0.5");
        file.defence.put("stop", "d20 per*0.5 ref*0.5 end*0.5 wis*0.5 spr*0.5");
        return file;
    }

    private static class RulesFileContent {
        Map<String, String> system = new HashMap<>();
        Map<String, String> attack = new HashMap<>();
        Map<String, String> defence = new HashMap<>();
    }
}
