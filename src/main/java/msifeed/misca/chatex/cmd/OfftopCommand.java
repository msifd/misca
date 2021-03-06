package msifeed.misca.chatex.cmd;

import msifeed.misca.chatex.ChatexRpc;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.Collections;
import java.util.List;

public class OfftopCommand extends CommandBase {
    @Override
    public String getName() {
        return "offtop";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("o");
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

        final String text = String.join(" ", args);
        ChatexRpc.broadcastOfftop((EntityPlayerMP) sender, text);
    }
}
