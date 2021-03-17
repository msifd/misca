package msifeed.misca.chatex.cmd;

import msifeed.misca.MiscaPerms;
import msifeed.misca.chatex.ChatexRpc;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.Collections;
import java.util.List;

public class GmGlobalCommand extends CommandBase {
    private static final List<String> ALIASES = Collections.singletonList("s");

    @Override
    public String getName() {
        return "gmgl";
    }

    @Override
    public List<String> getAliases() {
        return ALIASES;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/gmgl <text>";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return MiscaPerms.isGameMaster(sender);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayerMP)) return;

        final EntityPlayerMP player = (EntityPlayerMP) sender;
        final String text = String.join(" ", args).trim();

        if (!text.isEmpty())
            ChatexRpc.broadcastGameMasterGlobal(player, text);
    }
}
