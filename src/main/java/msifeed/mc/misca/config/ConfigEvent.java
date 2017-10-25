package msifeed.mc.misca.config;

import java.util.HashMap;

public class ConfigEvent {
    static class Collect extends ConfigEvent {
        HashMap<String, String> configs = new HashMap<>();
    }

    static class Override extends ConfigEvent {
        HashMap<String, String> configs = new HashMap<>();
    }

    static class Reload extends ConfigEvent {
    }

    public static class ReloadDone extends ConfigEvent {
    }
}
