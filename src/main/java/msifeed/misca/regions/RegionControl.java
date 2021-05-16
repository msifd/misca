package msifeed.misca.regions;

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
import java.util.List;
import java.util.stream.Stream;

public class RegionControl {
    private static final JsonConfig<RegionConfig> config = new JsonConfig<>("regions.json", TypeToken.get(RegionConfig.class));

    public static void init() {
        MinecraftForge.EVENT_BUS.register(RegionControl.class);
    }

    public static RegionConfig config() {
        return config.get();
    }

    public static void sync() throws Exception {
        config.sync();
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

    public static Stream<RegionConfig.Region> getLocalRules(World world, Vec3d pos) {
        final int dim = world.provider.getDimension();
        final List<RegionConfig.Region> regions = config.get().get(dim);
        if (regions == null || regions.isEmpty()) return Stream.empty();

        return regions.stream().filter(r -> r.contains(pos));
    }

    private static boolean isBlocked(World world, EntityLivingBase entity) {
        final int dim = world.provider.getDimension();
        final List<RegionConfig.Region> regions = config.get().get(dim);
        if (regions == null || regions.isEmpty()) return false;

        final Class<?> ec = entity.getClass();
        for (RegionConfig.Region r : regions) {
            if (!r.contains(entity.getPositionVector())) continue;
            for (Class<?> c : r.whitelist) {
                if (c.isAssignableFrom(ec))
                    return false;
            }
            for (Class<?> c : r.blacklist) {
                if (c.isAssignableFrom(ec))
                    return true;
            }
        }

        return false;
    }
}
