package msifeed.misca.chatex.cmd;

import msifeed.misca.MiscaPerms;
import msifeed.misca.chatex.ChatexRpc;
import msifeed.misca.chatex.ChatexUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

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
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return MiscaPerms.isGameMaster(sender);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayerMP)) return;
        if (args.length < 2) return;

        final EntityPlayerMP target = getPlayer(server, sender, args[0]);
        final String text = Stream.of(args).skip(1).collect(Collectors.joining(" "));

        final String formatted = ChatexUtils.fromAmpersandFormat(text);
        final String senderText = "[GMPM][" + target.getDisplayName() + "]: " + formatted;

        ChatexRpc.directRaw((EntityPlayerMP) sender, new TextComponentString(senderText));
        ChatexRpc.directRaw(target, new TextComponentString(formatted));
    }
}
