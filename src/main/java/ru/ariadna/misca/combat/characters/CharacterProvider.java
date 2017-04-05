package ru.ariadna.misca.combat.characters;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import ru.ariadna.misca.Misca;
import ru.ariadna.misca.combat.Combat;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CharacterProvider {
    private File charsDir;
    private Toml toml = new Toml();
    private TomlWriter tomlWriter = new TomlWriter();
    private Map<String, Character> chars = new HashMap();

    public void init() {
        charsDir = new File(Misca.config_dir, "chars");
        charsDir.mkdirs();

        reloadCharacters();
    }

    public void reloadCharacters() {
        FileFilter fileFilter = new WildcardFileFilter("*.toml");
        File[] files = charsDir.listFiles(fileFilter);

        chars.clear();
        for (File f : files) {
            Character c = toml.read(f).to(Character.class);
            if (c.name == null) continue; // Dirty check empty file
            chars.put(c.name, c);
        }
    }

    public void updateCharacter(Character c) {
        try {
            chars.put(c.name, c);
            tomlWriter.write(c, new File(charsDir, c.name + ".toml"));
        } catch (IOException e) {
            Combat.logger.error("Failed to make char stats file! {}", e);
        }
    }
}
