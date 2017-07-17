package ru.ariadna.misca.crabs.characters;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import net.minecraft.entity.player.EntityPlayer;
import ru.ariadna.misca.Misca;
import ru.ariadna.misca.MiscaUtils;
import ru.ariadna.misca.config.ConfigManager;
import ru.ariadna.misca.crabs.Crabs;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class CharacterProvider {
    private File char_dir;
    private Map<String, Character> characters = new HashMap<>();
    private Map<String, Consumer<Optional<Character>>> requests = new HashMap<>();

    public static boolean canEditCharacter(EntityPlayer player, String name) {
        return player.getDisplayName().equalsIgnoreCase(name) || MiscaUtils.isOp(player);
    }

    public void onInit() {
        char_dir = new File(ConfigManager.config_dir, "chars");
        char_dir.mkdirs();
    }

    public void request(String name, Consumer<Optional<Character>> consumer) {
        CharacterMessage request = new CharacterMessage();
        request.type = CharacterMessage.Type.REQUEST;
        request.name = name;
        requests.put(name, consumer);
        Misca.crabs.network.sendToServer(request);
    }

    void response(String name, Optional<Character> c) {
        Consumer<Optional<Character>> consumer = requests.remove(name);
        if (consumer != null) consumer.accept(c);
    }

    public Character get(String name) {
        Character c = characters.get(name);
        if (c == null) {
            c = readCharacter(name);
            if (c != null) characters.put(name, c);
        }
        return c;
    }

    public void update(EntityPlayer player, Character c) {
        if (!canEditCharacter(player, c.name)) return;
        characters.put(c.name, c);
        writeCharacter(c);
    }

    private Character readCharacter(String name) {
        File f = new File(char_dir, name + ".toml");

        if (!f.exists()) return null;
        if (!f.canRead()) {
            Crabs.logger.warn("Can't read character file at {}!", f);
            return null;
        }

        Toml toml = new Toml();
        Character c = toml.read(f).to(Character.class);
        return c.name == null ? null : c;
    }

    private void writeCharacter(Character c) {
        try {
            TomlWriter toml = new TomlWriter();
            toml.write(c, new File(char_dir, c.name + ".toml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
