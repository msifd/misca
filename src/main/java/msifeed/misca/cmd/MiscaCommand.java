package msifeed.misca.cmd;

import msifeed.misca.Misca;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;

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
        return "/misca <config>";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
//        return MiscaPerms.isGameMaster(sender);
        return super.checkPermission(server, sender);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 0)
            return Collections.singletonList("config");
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
            case "config":
                syncConfig(sender);
                break;
            default:
                sender.sendMessage(new TextComponentString(getUsage(sender)));
                break;
        }
    }

    private void syncConfig(ICommandSender sender) {
        try {
            ConfigManager.sync(Misca.MODID, Config.Type.INSTANCE);
            Misca.SHARED.load();
            sender.sendMessage(new TextComponentString("[Misca] Sync config ok"));
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(new TextComponentString(e.getMessage()));
        }
    }
}
