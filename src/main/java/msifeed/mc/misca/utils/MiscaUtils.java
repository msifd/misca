package msifeed.mc.misca.utils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.IChatComponent;

import java.util.stream.Stream;

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

    public static String roughRemoveFormatting(String raw) {
        if (raw.length() < 2) return raw;

        final StringBuilder sb = new StringBuilder(raw);
        int index = 0;
        while (index < sb.length() - 1) {
            if (sb.charAt(index) == '\u00A7') {
                sb.delete(index, index + 2);
            } else {
                index++;
            }
        }
        return sb.toString();
    }

    public static void notifyAround(EntityLivingBase center, int radius, IChatComponent msg) {
        EntityUtils.getPlayersAround(center, radius).forEach(player -> player.addChatMessage(msg));
    }

    public static void notifyAround(EntityLivingBase center1, EntityLivingBase center2, int radius, IChatComponent msg) {
        Stream.concat(
                EntityUtils.getPlayersAround(center1, radius),
                EntityUtils.getPlayersAround(center2, radius)
        )
                .distinct()
                .forEach(player -> player.addChatMessage(msg));
    }
}
