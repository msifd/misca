package msifeed.misca.regions;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegionConfig extends HashMap<Integer, List<RegionConfig.Region>> {
    @Nullable
    public Region get(int dim, String name) {
        final List<Region> regions = this.get(dim);
        if (regions == null) return null;

        for (Region r : regions) {
            if (r.name.equals(name)) return r;
        }

        return null;
    }

    public void add(int dim, Region region) {
        this.computeIfAbsent(dim, integer -> new ArrayList<>())
                .add(region);
    }

    public void delete(int dim, String name) {
        final List<Region> regions = this.get(dim);
        if (regions != null)
            regions.removeIf(r -> r.name.equals(name));
    }

    public static class Region {
        public String name = "";
        public @Nullable AxisAlignedBB aabb;
        public List<Class<?>> blacklist = new ArrayList<>();
        public List<Class<?>> whitelist = new ArrayList<>();

        public boolean contains(Vec3d pos) {
            return aabb == null || aabb.contains(pos);
        }
    }
}
