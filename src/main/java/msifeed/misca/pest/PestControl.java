package msifeed.misca.pest;

import com.google.gson.reflect.TypeToken;
import msifeed.sys.sync.JsonConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PestControl {
    private static final JsonConfig<PestConfig> config = new JsonConfig<>("pest-control.json", TypeToken.get(PestConfig.class));

    public static void init() {
        MinecraftForge.EVENT_BUS.register(PestControl.class);
    }

    public static PestConfig config() {
        return config.get();
    }

    public static void writeConfig() throws IOException {
        config.writeFile();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntitySpawn(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityPlayer) return;
        if (!(event.getEntity() instanceof EntityLivingBase)) return;

        if (isBlocked(event.getWorld(), (EntityLivingBase) event.getEntity())) {
            event.setCanceled(true);
        }
    }

    public static List<PestConfig.Rule> getLocalRules(World world, Vec3d pos) {
        final int dim = world.provider.getDimension();
        final List<PestConfig.Rule> rules = config.get().worlds.get(dim);
        if (rules == null || rules.isEmpty()) return Collections.emptyList();

        return rules.stream()
                .filter(r -> r.aabb == null || r.aabb.contains(pos))
                .collect(Collectors.toList());

    }

    private static boolean isBlocked(World world, EntityLivingBase entity) {
        final int dim = world.provider.getDimension();
        final List<PestConfig.Rule> rules = config.get().worlds.get(dim);
        if (rules == null || rules.isEmpty()) return false;

        final Class<?> ec = entity.getClass();
        for (PestConfig.Rule r : rules) {
            // Null aabb are global
            if (r.aabb != null && !r.aabb.contains(entity.getPositionVector())) continue;

            for (Class<?> c : r.classes) {
                if (ec.isAssignableFrom(c))
                    return true;
            }
        }

        return false;
    }
}
