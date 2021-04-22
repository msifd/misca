package msifeed.misca.chatex.cmd;

import msifeed.misca.MiscaPerms;
import msifeed.misca.chatex.ChatexRpc;
import msifeed.misca.chatex.ChatexUtils;
import msifeed.misca.logdb.LogDB;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GmPmCommand extends CommandBase {
    @Override
    public String getName() {
        return "gmpm";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/gmpm <name> <text>";
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        if (args.length == 1)
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        else
            return Collections.emptyList();
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return MiscaPerms.isGameMaster(sender);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender cmdSender, String[] args) throws CommandException {
        if (!(cmdSender instanceof EntityPlayerMP)) return;
        if (args.length < 2) return;

        final EntityPlayerMP sender = (EntityPlayerMP) cmdSender;
        final EntityPlayerMP target = getPlayer(server, cmdSender, args[0]);
        final String text = Stream.of(args).skip(1).collect(Collectors.joining(" "));

        final String formatted = ChatexUtils.fromAmpersandFormat(text);
        final String senderText = "[GMPM][" + target.getDisplayNameString() + "]: " + formatted;

        ChatexRpc.sendGameMasterPM(sender, new TextComponentString(senderText));
        ChatexRpc.sendGameMasterPM(target, new TextComponentString(formatted));
        LogDB.INSTANCE.log(sender, "raw", new TextComponentString(senderText).getUnformattedText());
    }
}
