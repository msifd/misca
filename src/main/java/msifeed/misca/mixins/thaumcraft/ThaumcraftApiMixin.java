package msifeed.misca.mixins.thaumcraft;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectEventProxy;
import thaumcraft.api.aspects.AspectList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mixin(ThaumcraftApi.class)
public class ThaumcraftApiMixin {
    private static ExecutorService executorService;

    private static ExecutorService getExecutor(){
        if (executorService == null) {
            executorService = Executors.newCachedThreadPool();
            Thaumcraft.log.info("[MISCA] ThaumCraft - Creating a CachedThreadPool to register ComplexTags");
        }
        return executorService;
    }

    /**
     * @author EverNife
     * @reason Try to do ThaumCraft Post-Init assync
     *
     * This method can take up to 9%-12% of the load time.
     * Try to make it assync and hope it doesn't break anything
     */
    @Overwrite
    @Deprecated
    public static void registerComplexObjectTag(ItemStack item, AspectList aspects) {
        getExecutor().submit(() -> {
            (new AspectEventProxy()).registerComplexObjectTag(item, aspects);
        });
    }

    /**
     * @author EverNife
     * @reason Try to do ThaumCraft Post-Init assync
     *
     * This method can take up to 7%-10% of the load time.
     * Try to make it assync and hope it doesn't break anything
     */
    @Overwrite
    @Deprecated
    public static void registerComplexObjectTag(String oreDict, AspectList aspects) {
        getExecutor().submit(() -> {
            (new AspectEventProxy()).registerComplexObjectTag(oreDict, aspects);
        });
    }
}
