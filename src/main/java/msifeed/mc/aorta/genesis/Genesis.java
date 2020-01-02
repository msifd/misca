package msifeed.mc.aorta.genesis;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import msifeed.mc.aorta.genesis.blocks.BlockGenerator;
import msifeed.mc.aorta.genesis.content.EmptySignBlock;
import msifeed.mc.aorta.genesis.items.ItemGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;

public class Genesis {
    public static final String MODID = "misca";
    private static final JsonParser jsonParser = new JsonParser();
    private static final ImmutableMap<GenesisTrait, Generator> generators = ImmutableMap.<GenesisTrait, Generator>builder()
            .put(GenesisTrait.block, new BlockGenerator())
            .put(GenesisTrait.item, new ItemGenerator())
            .build();

    private static Logger log = LogManager.getLogger("Aorta.Gen");
    private static boolean abortLoading = false;

    public void init() {
        EmptySignBlock.register();

        for (Generator g : generators.values())
            g.init();
        generate();
    }

    public void generate() {
        final File mcRootDir = Loader.instance().getConfigDir().getParentFile();
        final File genesisDir = new File(mcRootDir, "genesis");
        if (!genesisDir.exists())
            genesisDir.mkdirs();

        try {
            loadFolder(genesisDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (abortLoading) {
            log.warn("Abort due to genesis errors!");
            FMLCommonHandler.instance().handleExit(1);
        }
    }

    private void loadFolder(File genesisDir) throws IOException {
        Files.walk(genesisDir.toPath())
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".json"))
                .forEach(this::loadFile);
    }

    private void loadFile(Path path) {
        try {
            final JsonElement parsed = jsonParser.parse(Files.newBufferedReader(path));
            if (!parsed.isJsonArray())
                return;
            for (JsonElement je : parsed.getAsJsonArray())
                if (je.isJsonObject())
                    loadObject(je.getAsJsonObject());
        } catch (IOException e) {
            log.error("IO error '{}' at file {}", e.getMessage(), path);
            abortLoading = true;
        } catch (JsonParseException e) {
            log.error("Parse error '{}' at file {}", e.getMessage(), path);
            abortLoading = true;
        } catch (Exception e) {
            log.error("Exception '{}' at file {}", e, path);
            abortLoading = true;
        }
    }

    private void loadObject(JsonObject json) {
        final HashSet<GenesisTrait> traits = parseTraits(json);
        final Optional<GenesisTrait> generatorTrait = traits.stream()
                .filter(generators::containsKey)
                .findFirst();
        generatorTrait.ifPresent(genesisTrait -> {
            generators.get(genesisTrait).generate(json, traits);
        });
    }

    private HashSet<GenesisTrait> parseTraits(JsonObject json) {
        final HashSet<GenesisTrait> traits = new HashSet<>();
        for (JsonElement je : json.getAsJsonArray("traits")) {
            try {
                traits.add(GenesisTrait.valueOf(je.getAsJsonPrimitive().getAsString().toLowerCase()));
            } catch (IllegalArgumentException e) {
            }
        }
        return traits;
    }
}
