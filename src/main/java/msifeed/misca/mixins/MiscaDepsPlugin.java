package msifeed.misca.mixins;

import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MiscaDepsPlugin implements IFMLLoadingPlugin {
    private final Logger LOG = LogManager.getLogger("Misca-Deps");

    private final Map<String, String> REFERENCES = Stream.of(
            "com.flansmod.common.guns.ItemGun",
            "thaumcraft.common.items.casters.ItemCaster",
            "electroblob.wizardry.item.ItemWand",
            "electroblob.wizardry.item.ItemScroll",
            "slimeknights.tconstruct.library.tools.ranged.BowCore",
            "slimeknights.tconstruct.tools.ranged.item.CrossBow"
    ).collect(Collectors.toMap(s -> s.replace('.', '/') + ".class", s -> s));

    private final List<String> REFERENCE_FILES = new ArrayList<>(REFERENCES.keySet());

    @Override
    public void injectData(Map<String, Object> data) {
        if ((boolean) data.get("runtimeDeobfuscationEnabled")) {
            preloadDependencies(Paths.get("mods"));
        } else {
            LOG.info("Skip preload");
        }

        LOG.info("Add thirdparty mixins");
        Mixins.addConfiguration("mixins.thirdparty.misca.json");
    }

    private void preloadDependencies(Path modsDir) {
        LOG.info("Preload dependency mods");

        if (!Files.isDirectory(modsDir)) {
            LOG.error("Missing mods dir");
            return;
        }

        try {
            Files.walk(modsDir)
                    .filter(this::isJar)
                    .forEach(file -> preloadJar(modsDir, file));
        } catch (IOException e) {
            LOG.throwing(e);
        }
    }

    private boolean isJar(Path path) {
        final String name = path.toString();
        return name.endsWith(".jar") || name.endsWith(".zip");
    }

    private void preloadJar(Path modsDir, Path modFile) {
        final List<String> refs = findReferenceClasses(modFile);
        if (refs.isEmpty()) return;

        final Path jar = modsDir.relativize(modFile);
        LOG.info("Preload jar " + jar);
        REFERENCE_FILES.removeAll(refs);

        try {
            ((LaunchClassLoader) getClass().getClassLoader()).addURL(modFile.toUri().toURL());
            CoreModManager.getReparseableCoremods().add(jar.toString());
        } catch (Exception e) {
            LOG.warn("Failed to preload " + jar, e);
        }
    }

    private List<String> findReferenceClasses(Path path) {
        if (REFERENCE_FILES.isEmpty())
            return Collections.emptyList();

        try (FileSystem fs = FileSystems.newFileSystem(path, null)) {
            return REFERENCE_FILES.stream()
                    .filter(ref -> Files.exists(fs.getPath(ref)))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            LOG.warn(e);
        }

        return Collections.emptyList();
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
