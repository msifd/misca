package msifeed.misca.pest;

import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PestConfig {
    public Map<Integer, List<Rule>> worlds = new HashMap<>();

    @Nullable
    public Rule get(int dim, String name) {
        final List<Rule> rules = worlds.get(dim);
        if (rules == null) return null;

        for (Rule r : rules) {
            if (r.name.equals(name)) return r;
        }

        return null;
    }

    public void add(int dim, Rule rule) {
        worlds.computeIfAbsent(dim, integer -> new ArrayList<>())
                .add(rule);
    }

    public void delete(int dim, String name) {
        final List<Rule> rules = worlds.get(dim);
        if (rules != null)
            rules.removeIf(r -> r.name.equals(name));
    }

    public static class Rule {
        public String name = "";
        public List<Class<?>> classes = new ArrayList<>();
        @Nullable
        public AxisAlignedBB aabb;
    }
}
