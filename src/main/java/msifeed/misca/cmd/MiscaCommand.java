package msifeed.misca.cmd;

import msifeed.misca.Misca;
import msifeed.misca.MiscaPerms;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class MiscaCommand extends CommandBase {
    @Override
    public String getName() {
        return "misca";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/misca <reload>";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return MiscaPerms.isAdmin(sender);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length > 0)
            return Collections.singletonList("reload");
        else
            return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        switch (args[0]) {
            case "reload":
                reloadConfig(sender);
                break;
            default:
                sender.sendMessage(new TextComponentString(getUsage(sender)));
                break;
        }
    }

    private void reloadConfig(ICommandSender sender) {
        try {
            Misca.syncConfig();
            sender.sendMessage(new TextComponentString("[Misca] Reload config ok"));
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(new TextComponentString(e.getMessage()));
        }
    }
}
