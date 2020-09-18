package msifeed.misca.genesis.rules;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class RuleLoader<T extends IGenesisRule> {
    private final Type listOfRulesType;
    private final Gson gson = new Gson();

    public RuleLoader(Class<T> ruleType) {
        this.listOfRulesType = TypeToken.getParameterized(List.class, ruleType).getType();
    }

    public void load(File genesisDir) throws IOException {
        Files.walk(genesisDir.toPath())
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".json"))
                .forEach(this::loadFile);
    }

    private void loadFile(Path path) {
        try {
            final List<T> rules = gson.fromJson(Files.newBufferedReader(path), listOfRulesType);
            for (IGenesisRule rule : rules)
                rule.generate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
