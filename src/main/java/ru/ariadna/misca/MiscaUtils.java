package ru.ariadna.misca;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.entity.player.EntityPlayer;

public class MiscaUtils {
    public static String localize(String raw) {
        return LanguageRegistry.instance().getStringLocalization(raw);
    }

    public static boolean isOp(EntityPlayer player) {
        return false; // TODO
    }
}
