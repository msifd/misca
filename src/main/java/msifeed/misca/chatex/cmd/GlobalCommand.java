package msifeed.misca.chatex.cmd;

import msifeed.misca.chatex.ChatexRpc;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.Collections;
import java.util.List;

public class GlobalCommand extends CommandBase {
    @Override
    public String getName() {
        return "global";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("g");
    }
    @Override
    public String getUsage(ICommandSender sender) {
        return "/g <global text>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerMP)) return;

        final String text = String.join(" ", args);

        ChatexRpc.sendGlobal((EntityPlayerMP) sender, text);
    }
}
