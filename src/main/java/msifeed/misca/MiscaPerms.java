package msifeed.misca;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

public final class MiscaPerms {
    private static final String gameMaster = "misca.gm";

    public static void register() {
        PermissionAPI.registerNode(gameMaster, DefaultPermissionLevel.NONE, "Gives access to GameMaster abilities");
    }

    public static boolean isGameMaster(EntityPlayer player) {
        return PermissionAPI.hasPermission(player, gameMaster);
    }
}
