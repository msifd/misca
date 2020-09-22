package msifeed.misca;

import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

public final class MiscaPerms {
    public static final String gameMaster = "misca.gm";

    public static void register() {
        PermissionAPI.registerNode(gameMaster, DefaultPermissionLevel.NONE, "Gives access to GameMaster abilities");
    }
}
