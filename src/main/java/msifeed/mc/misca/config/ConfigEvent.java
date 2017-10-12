package msifeed.mc.misca.config;

import java.io.Serializable;
import java.util.HashMap;

public class ConfigEvent {
    public static class Sync extends ConfigEvent {
        public HashMap<String, Serializable> configs = new HashMap<>();
    }

    public static class Reload extends ConfigEvent {
    }
}
