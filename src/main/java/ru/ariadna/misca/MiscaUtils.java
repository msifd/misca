package ru.ariadna.misca;

import cpw.mods.fml.common.registry.LanguageRegistry;

public class MiscaUtils {
    public static String localize(String raw) {
        return LanguageRegistry.instance().getStringLocalization(raw);
    }
}
