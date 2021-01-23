package msifeed.misca;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

public final class MiscaPerms {
    public static boolean isAdmin(ICommandSender sender) {
        return sender.canUseCommand(4, "misca.admin");
    }

    public static boolean isGameMaster(ICommandSender sender) {
        return sender.canUseCommand(3, "misca.gm");
    }

    public static boolean userLevel(ICommandSender sender, String perm) {
        return sender.canUseCommand(0, perm);
    }
}
