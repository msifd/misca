package msifeed.misca.cmd;

import msifeed.misca.MiscaPerms;
import msifeed.misca.charsheet.CharResource;
import msifeed.misca.charsheet.ICharsheet;
import msifeed.misca.charsheet.cap.CharsheetProvider;
import msifeed.misca.charsheet.cap.CharsheetSync;
import msifeed.misca.logdb.LogDB;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CharsheetCommand extends CommandBase {
    @Override
    public String getName() {
        return "charsheet";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/charsheet <who> <ord seal> [<add set> <value>]";
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        switch (args.length) {
            case 1:
                return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
            case 2:
                return getListOfStringsMatchingLastWord(args,"ord", "seal");
            case 3:
                return getListOfStringsMatchingLastWord(args,"add", "set");
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) throw new SyntaxErrorException("Expected at least 2 args");

        final EntityPlayerMP player = getPlayer(server, sender, args[0]);

        final CharResource res;
        if (args[1].equals("ord")) res = CharResource.ord;
        else if (args[1].equals("seal")) res = CharResource.seal;
        else throw new SyntaxErrorException("Unknown resource: " + args[1]);

        final ICharsheet sheet = CharsheetProvider.get(player);
        if (args.length >= 4 && MiscaPerms.isGameMaster(sender)) {
            final boolean set = args[2].equalsIgnoreCase("set");
            final int value = parseInt(args[3], 0, 100);
            final int curr = sheet.resources().get(res);
            final int modified = set ? value : value + curr;

            sheet.resources().set(res, modified);
            CharsheetSync.sync(player);

            final String msg = String.format("Change %s's %s from %d to %d", player.getName(), res, curr, modified);
            sender.sendMessage(new TextComponentString(msg));
            LogDB.INSTANCE.log(sender, "resource", msg);
        } else {
            sender.sendMessage(new TextComponentString(String.format("%s's %s: %d", player.getDisplayNameString(), res, sheet.resources().get(res))));
        }
    }
}
