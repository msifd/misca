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

public class CalcRulesProvider {
    private File dataFile;
    private Toml toml = new Toml();

    private EnumMap<Action, CalcRule> rules = new EnumMap<>(Action.class);

    public void init() {
        dataFile = new File(Combat.configDir, "combat_rules.toml");
        if (!dataFile.exists()) {
            writeDefaultRules();
        }

        reload();
    }

    public CalcRule getRule(Action act) {
        return rules.get(act);
    }

    public boolean reload() {
        if (dataFile.exists()) {
            Combat.logger.info("Combat rules file not found!");
        }

        try {
            Map<String, String> content = toml.read(dataFile).to(HashMap.class);
            // Fix empty config file
            if (content == null) {
                content = getDefaultRules();
            }

            EnumMap<Action, CalcRule> new_rules = new EnumMap<>(Action.class);
            for (Map.Entry<String, String> raw_rule : content.entrySet()) {
                try {
                    Action act = Action.valueOf(raw_rule.getKey().toUpperCase());
                    CalcRule rule = new CalcRule(raw_rule.getValue());
                    new_rules.put(act, rule);
                } catch (CalcRule.RuleParseException e) {
                    Combat.logger.error("Error while parsing combat rules for action '{}'! {}", raw_rule.getKey(), e);
                    return false;
                } catch (IllegalArgumentException e) {
                    Combat.logger.error("Error while parsing combat rules! Wrong action name: {}", raw_rule.getKey());
                    return false;
                }
            }
            rules = new_rules;
        } catch (IllegalStateException e) {
            Combat.logger.error("Error while reading combat rules config! {}", e);
            return false;
        }
        return true;
    }

    private void writeDefaultRules() {
        try {
            TomlWriter tomlWriter = new TomlWriter();
            tomlWriter.write(getDefaultRules(), dataFile);
        } catch (IOException e) {
            Combat.logger.error("Failed to write default rules! {}", e);
        }
    }

    private Map<String, String> getDefaultRules() {
        Map<String, String> content = new HashMap<>();
        content.put("init", "d20 ref*0.5 det*0.5");
        content.put("hit", "d10 str*1.5 end*0.5");
        content.put("shoot", "d10 per*1.5 ref*0.5");
        content.put("magicatk", "d10 spr*1.5 wis*0.5");
        content.put("slam", "d10 end*0.5 ref*0.5 det*0.5");
        content.put("other", "d10 str*0.5 per*0.5 ref*0.5 det*0.5");
        content.put("special", "d10 wis*1.5 spr*0.5");
        content.put("flee", "d10 per*0.5 ref*0.5 end*0.5 det*0.5 wis*0.5 spr*0.5");
        content.put("safe", "d10 det*1.5 end*0.5");
        content.put("defence", "d10 end*1.5 str*0.5");
        content.put("dodge", "d10 ref*1.5 per*0.5");
        content.put("magicdef", "d10 spr*1 wis*1");
        content.put("dum", "d10 str*0.5 per*0.5 det*0.5");
        content.put("stop", "d20 per*0.5 ref*0.5 end*0.5 wis*0.5 spr*0.5");
        return content;
    }
}
