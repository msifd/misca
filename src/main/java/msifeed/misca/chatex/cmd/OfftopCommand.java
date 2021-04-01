package msifeed.misca.chatex.cmd;

import msifeed.misca.Misca;
import msifeed.misca.chatex.ChatexRpc;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class OfftopCommand extends CommandBase {
    @Override
    public String getName() {
        return "offtop";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/offtop <text>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerMP)) return;

        final int range = Misca.getSharedConfig().chat.offtopRange;
        final String text = String.join(" ", args);

        ChatexRpc.broadcastOfftop((EntityPlayerMP) sender, range, text);
    }
}
