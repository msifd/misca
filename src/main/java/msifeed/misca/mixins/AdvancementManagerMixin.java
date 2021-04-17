package msifeed.misca.mixins;

import net.minecraft.advancements.AdvancementManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AdvancementManager.class)
public class AdvancementManagerMixin {
    @Redirect(method = "loadCustomAdvancements", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;error(Ljava/lang/String;Ljava/lang/Throwable;)V", remap = false))
    private void onCustomLogError(Logger logger, String message, Throwable t) {
        logSomeErrors(logger, message, t);
    }

    @Redirect(method = "loadBuiltInAdvancements", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;error(Ljava/lang/String;Ljava/lang/Throwable;)V", remap = false))
    private void onBuildInLogError(Logger logger, String message, Throwable t) {
        logSomeErrors(logger, message, t);
    }

    private void logSomeErrors(Logger logger, String message, Throwable t) {
        if (!message.startsWith("Parsing"))
            logger.error(message, t);
    }
}
