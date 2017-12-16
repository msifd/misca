package msifeed.mc.misca.utils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;

public class MiscaUtils {
    public static String l10n(String key) {
        return LanguageRegistry.instance().getStringLocalization(key);
    }

    public static String l10n(String key, Object... args) {
        return String.format(LanguageRegistry.instance().getStringLocalization(key), args);
    }

    public static boolean isOp(ICommandSender sender) {
        if (sender instanceof EntityPlayerMP) {
            ServerConfigurationManager scm = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager();
            EntityPlayerMP pl = (EntityPlayerMP) sender;
            if (scm.func_152596_g(pl.getGameProfile())) {
                return true;
            }
        } else if (sender instanceof MinecraftServer) {
            return true;
        }
        return false;
    }
}
