package msifeed.misca;

import net.minecraft.command.ICommandSender;

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
