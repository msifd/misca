package msifeed.misca;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

public final class MiscaPerms {
    private static final String gameMaster = "misca.gm";

    public static void register() {
        PermissionAPI.registerNode(gameMaster, DefaultPermissionLevel.NONE, "Gives access to GameMaster abilities");
    }

    public static boolean isGameMaster(ICommandSender sender) {
        if (sender instanceof EntityPlayer)
            return PermissionAPI.hasPermission((EntityPlayer) sender, gameMaster);
        else
            return sender instanceof MinecraftServer || sender instanceof RConConsoleSource;
    }
}
