package msifeed.misca.mixins.loader;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModClassLoader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.transformer.MixinProcessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Thanks to https://github.com/CrucibleMC/Grimoire for the idea
 */
@Mixin(value = Loader.class, remap = false)
public class LoaderMixin {
    private static final Logger LOG = LogManager.getLogger("Misca-Core");
    private static final Set<String> DEPS = Stream.of("flansmod", "ebwizardry", "techguns", "thaumcraft")
            .collect(Collectors.toSet());

    @Shadow
    private List<ModContainer> mods;
    @Shadow
    private ModClassLoader modClassLoader;

    private static final String TRANSITION = "Lnet/minecraftforge/fml/common/LoadController;transition(Lnet/minecraftforge/fml/common/LoaderState;Z)V";

    @Inject(method = "loadMods", at = @At(value = "INVOKE", target = TRANSITION, ordinal = 1))
    private void beforeConstructingMods(CallbackInfo ci) {
        LOG.info("Preload Misca dependencies");

        for (ModContainer mod : mods) {
            if (!DEPS.contains(mod.getModId())) continue;

            try {
                LOG.info("Preload {}. Path: {}", mod.getModId(), mod.getSource());
                modClassLoader.addFile(mod.getSource());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        LOG.info("Add compatibility mixins");
        Mixins.addConfiguration("mixins.compat.misca.json");
        updateCurrentMixinEnv();
    }

    private void updateCurrentMixinEnv() {
        final MixinEnvironment env = MixinEnvironment.getCurrentEnvironment();
        final Object transformer = env.getActiveTransformer();

        try {
            final Field processorField = transformer.getClass().getDeclaredField("processor");
            processorField.setAccessible(true);
            final Object processor = processorField.get(transformer);

            final Method selectMethod = MixinProcessor.class.getDeclaredMethod("selectConfigs", MixinEnvironment.class);
            selectMethod.setAccessible(true);
            selectMethod.invoke(processor, env);

            final Method prepareMethod = MixinProcessor.class.getDeclaredMethod("prepareConfigs", MixinEnvironment.class);
            prepareMethod.setAccessible(true);
            prepareMethod.invoke(processor, env);

        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
