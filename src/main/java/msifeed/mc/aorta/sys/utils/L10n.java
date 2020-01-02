package msifeed.mc.aorta.sys.utils;

import cpw.mods.fml.common.registry.LanguageRegistry;

public class L10n {
    public static String tr(String key) {
        return LanguageRegistry.instance().getStringLocalization(key);
    }

    public static String fmt(String key, Object... args) {
        return String.format(LanguageRegistry.instance().getStringLocalization(key), args);
    }
}
